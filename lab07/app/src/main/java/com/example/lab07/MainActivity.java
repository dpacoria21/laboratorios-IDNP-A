package com.example.lab07;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Esto carga el activity_main.xml, el cual a su vez carga
        // automáticamente tu EdificacionesFragment gracias a la etiqueta android:name
        setContentView(R.layout.activity_main);
    }
}