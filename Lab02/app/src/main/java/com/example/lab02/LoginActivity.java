package com.example.lab02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab02.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String accountEntityString;
    private static final String ACCOUNTS_FILE = "cuentas.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EditText edtUsername = binding.edtUsername;
        EditText edtPassword = binding.edtPassword;
        Button btnLogin = binding.btnLogin;
        Button btnAddAccount = binding.btnAddAccount;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                if (checkCredentials(username, password)) {
                    Toast.makeText(getApplicationContext(), "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("USERNAME", username);
                    activityResultLauncher2.launch(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Cuenta no encontrada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddAccount.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
            activityResultLauncher.launch(intent);

        });
    }
    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    //nothing yet
                }
            }
    );
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Integer resultCode = o.getResultCode();
                    if (resultCode == AccountActivity.ACCOUNT_ACEPTAR) {
                        Intent data = o.getData();
                        accountEntityString = data.getStringExtra(AccountActivity.ACCOUNT_RECORD);
                        saveAccountToFile();
                        Toast.makeText(getApplicationContext(), "Cuenta creada exitosamente", Toast.LENGTH_LONG).show();
                    } else if (resultCode.equals(AccountActivity.ACCOUNT_CANCELAR)) {
                        Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", "Cancelado");
                    }
                }
            }
    );
    private void saveAccountToFile() {
        try {
            FileOutputStream fos = openFileOutput(ACCOUNTS_FILE, MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            Log.v("string del account entity",accountEntityString);
            // Escribe los datos en el archivo
            osw.write(accountEntityString);
            osw.write("\n");
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LoginActivity", "Error saving account to file", e);
        }
    }
    private boolean checkCredentials(String username, String password) {
        try {
            // Abre el archivo en modo lectura
            FileInputStream fis = openFileInput(ACCOUNTS_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            Gson gson=new Gson();
            String line;
            // Lee cada línea del archivo
            while ((line = br.readLine()) != null) {
                Log.v("contenido",line);
                // Usa Gson para convertir la línea de JSON en un objeto AccountEntity
                AccountEntity account = gson.fromJson(line, AccountEntity.class);

                if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                    return true; // Las credenciales coinciden
                }

            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LoginActivity", "Error checking credentials", e);
        }
        return false;
    }
}