package com.example.lab4_fragments;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para cargar y manejar la lista de edificaciones.
 */
public class BuildingRepository {
    private List<Building> buildingList = new ArrayList<>();

    public BuildingRepository(Context context) {
        loadBuildings(context);
    }

    private void loadBuildings(Context context) {
        try {
            InputStream is = context.getAssets().open("edificaciones.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    String title = parts[0];
                    String category = parts[1];
                    String description = parts[2];
                    String imageName = parts[3];
                    double latitude = Double.parseDouble(parts[4]);
                    double longitude = Double.parseDouble(parts[5]);
                    int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
                    buildingList.add(new Building(title, category, description, imageResId, latitude, longitude));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Building> getBuildingList() {
        return buildingList;
    }
}
