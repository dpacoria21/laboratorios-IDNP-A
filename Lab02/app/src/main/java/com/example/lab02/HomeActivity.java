package com.example.lab02;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String accountName = getIntent().getStringExtra("USERNAME");
        // Buscar el TextView y configurar el mensaje de bienvenida
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        if (accountName != null && !accountName.isEmpty()) {
            welcomeMessage.setText("Bienvenido, " + accountName);
        } else {
            welcomeMessage.setText("Bienvenido, Usuario");
        }
    }
}
