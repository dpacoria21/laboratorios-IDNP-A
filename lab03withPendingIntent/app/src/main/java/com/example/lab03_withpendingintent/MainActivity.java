package com.example.lab03_withpendingintent;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.example.lab03_withpendingintent.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private BatteryBroadcastReceiver  batteryBroadcastReceiver = new BatteryBroadcastReceiver();
    private IntentFilter intentFilter = new IntentFilter();
    private ActivityMainBinding binding;

    TextView porcentaje, voltaje, salud, tipoBateria, tipoCarga, temp, estadoCarga;

    public static final String TAG = "BatteryBroadcastReceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
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
    protected void onPause() {
        unregisterReceiver(batteryBroadcastReceiver);
        Log.d(TAG, "BroadcastReceiver desregistrado satisfactoriamente.");
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(batteryBroadcastReceiver, intentFilter);
        Log.d(TAG, "BroadcastReceiver registrado satisfactoriamente.");
        super.onResume();
    }

    private void readFromFile(){
        try {
            FileInputStream fileInputStream = openFileInput("datos.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String text="";
            while ((text = bufferedReader.readLine()) != null) {
                stringBuilder.append(text).append("\n");
            }
            String[] data = stringBuilder.toString().split("\n");
            Log.v("textAAAA",data[4]);
            porcentaje.setText(data[0]);
            voltaje.setText(data[1]);
            salud.setText(data[2]);
            tipoBateria.setText(data[3]);
            tipoCarga.setText(data[4]);
            temp.setText(data[5]);
            estadoCarga.setText(data[6]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        /*
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
}