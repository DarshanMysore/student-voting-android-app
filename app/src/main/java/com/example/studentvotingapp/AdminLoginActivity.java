package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etUser = findViewById(R.id.etAdminUser);
        etPass = findViewById(R.id.etAdminPass);
        btnLogin = findViewById(R.id.btnAdminLogin);

        SharedPreferences sp = getSharedPreferences("AdminData", MODE_PRIVATE);

        // 🔹 Ensure default exists (only first time)
        if (!sp.contains("admin_username")) {
            sp.edit()
                    .putString("admin_username", "admin")
                    .putString("admin_password", "admin123")
                    .apply();
        }

        btnLogin.setOnClickListener(v -> {

            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (TextUtils.isEmpty(user)) {
                etUser.setError("Enter username");
                return;
            }

            if (TextUtils.isEmpty(pass)) {
                etPass.setError("Enter password");
                return;
            }

            // 🔹 ALWAYS fetch latest saved values
            String savedUser = sp.getString("admin_username", "admin");
            String savedPass = sp.getString("admin_password", "admin123");

            // 🔍 DEBUG (remove later)
            Toast.makeText(this,
                    "Saved: " + savedUser + " / " + savedPass,
                    Toast.LENGTH_LONG).show();

            if (user.equals(savedUser) && pass.equals(savedPass)) {

                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}