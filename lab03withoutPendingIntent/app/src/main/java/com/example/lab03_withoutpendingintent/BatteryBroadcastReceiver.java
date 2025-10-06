package com.example.lab03_withoutpendingintent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.io.OutputStreamWriter;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            String healthStatus;
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthStatus = "Good";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthStatus = "Overheat";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthStatus = "Dead";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthStatus = "Over Voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthStatus = "Unspecified Failure";
                    break;
                default:
                    healthStatus = "Unknown";
                    break;
            }
            String type = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            String chargingSource;
            switch (chargePlug) {
                case BatteryManager.BATTERY_PLUGGED_USB:
                    chargingSource = "USB";
                    break;
                case BatteryManager.BATTERY_PLUGGED_AC:
                    chargingSource = "AC Adapter";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    chargingSource = "Wireless";
                    break;
                default:
                    chargingSource = "Not Plugged";
                    break;
            }
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String chargingStatus;
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    chargingStatus = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    chargingStatus = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    chargingStatus = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    chargingStatus = "Not Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    chargingStatus = "Unknown";
                    break;
                default:
                    chargingStatus = "Unknown";
                    break;
            }



            int scale=intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            Log.v("nivel",String.valueOf(level));
            Log.v("scale",String.valueOf(scale));
            float batteryPct = level * 100 / (float)scale;
            writeToFile(context, level, voltage, healthStatus, type, chargingSource, temperature, chargingStatus);

        }

    }
    private void writeToFile(Context context, int level, int voltage, String healthStatus, String type,
                             String chargingSource, int temperature, String chargingStatus) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("datos.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(level + "\n");
            outputStreamWriter.write(voltage + "\n");
            outputStreamWriter.write(healthStatus + "\n");
            outputStreamWriter.write(type + "\n");
            outputStreamWriter.write(chargingSource + "\n");
            outputStreamWriter.write(temperature + "\n");
            outputStreamWriter.write(chargingStatus + "\n");
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("BatteryBroadcastReceiver", "Error al escribir en el archivo", e);
        }
    }
}
