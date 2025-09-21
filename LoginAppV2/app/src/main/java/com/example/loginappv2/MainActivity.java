package com.example.loginappv2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginappv2.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "usuarios.txt";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EditText edtFirstname = binding.edtFirstname;
        EditText edtLastname  = binding.edtLastname;
        EditText edtEmail     = binding.edtEmail;
        EditText edtPhone     = binding.edtPhone;
        EditText edtBlood     = binding.edtBlood;

        Button btnSave = binding.btnSave;
        Button btnShow = binding.btnShow;

        // Guardar en archivo de texto
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "Nombre: " + edtFirstname.getText().toString() + "\n" +
                        "Apellido: " + edtLastname.getText().toString() + "\n" +
                        "Correo: " + edtEmail.getText().toString() + "\n" +
                        "Teléfono: " + edtPhone.getText().toString() + "\n" +
                        "Grupo Sanguíneo: " + edtBlood.getText().toString() + "\n" +
                        "------------------------\n";

                // Sobrescribe el archivo en vez de agregar
                try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                    fos.write(data.getBytes());
                    Toast.makeText(MainActivity.this, "Datos guardados (archivo limpio)", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        // Leer y mostrar en Log
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (FileInputStream fis = openFileInput(FILE_NAME);
                     InputStreamReader isr = new InputStreamReader(fis);
                     BufferedReader br = new BufferedReader(isr)) {

                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    Log.d(TAG, "Contenido del archivo:\n" + sb.toString());
                    Toast.makeText(MainActivity.this, "Datos mostrados en Logcat", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error al leer archivo", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}