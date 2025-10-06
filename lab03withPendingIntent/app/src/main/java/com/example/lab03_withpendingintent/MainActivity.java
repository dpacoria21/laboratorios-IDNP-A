package com.example.lab03_withpendingintent;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.example.lab03_withpendingintent.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    TextView porcentaje, voltaje, salud, tipoBateria, tipoCarga, temp, estadoCarga;

    public static final String TAG = "BatteryBroadcastReceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        porcentaje = binding.textCntPorcentaje;
        voltaje = binding.textCntVoltaje;
        salud = binding.textCntSalud;
        temp = binding.textCntTemp;
        estadoCarga = binding.textCntEstadoCarga;
        tipoBateria = binding.textCntTipo;
        tipoCarga = binding.textCntEstadoCarga;

        readFromFile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Creamos el PendingIntent que lanzará el BroadcastReceiver
        Intent batteryIntent = new Intent(this, BatteryBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                batteryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Recuperamos el estado de la batería sin registrar un BroadcastReceiver directamente
        Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus != null) {
            try {
                pendingIntent.send(this, 0, batteryStatus);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "PendingIntent enviado al BroadcastReceiver.");
        readFromFile();
    }

    private void readFromFile() {
        try {
            FileInputStream fileInputStream = openFileInput("datos.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String text = "";
            while ((text = bufferedReader.readLine()) != null) {
                stringBuilder.append(text).append("\n");
            }
            String[] data = stringBuilder.toString().split("\n");
            if (data.length >= 7) {
                porcentaje.setText(data[0]);
                voltaje.setText(data[1]);
                salud.setText(data[2]);
                tipoBateria.setText(data[3]);
                tipoCarga.setText(data[4]);
                temp.setText(data[5]);
                estadoCarga.setText(data[6]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}