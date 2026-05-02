package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUsn, etPassword;
    Button btnLogin;
    TextView tvRegister, tvAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsn = findViewById(R.id.etUsn);
        etPassword = findViewById(R.id.etPassword); // make sure this exists in XML
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvAdmin = findViewById(R.id.tvAdmin);

        tvAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoginActivity.class))
        );

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void loginUser() {

        String usn = etUsn.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(usn)) {
            etUsn.setError("Enter USN");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter Password");
            return;
        }

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);

        // 🔹 Get saved password from register
        String savedPassword = sp.getString(usn + "_password", null);

        if (savedPassword != null && savedPassword.equals(password)) {

            // 🔹 Get real data
            String name = sp.getString(usn + "_name", "N/A");
            String branch = sp.getString(usn + "_branch", "N/A");

            // 🔹 Save current session
            sp.edit()
                    .putString("currentUser", usn)
                    .putString("name", name)
                    .putString("branch", branch)
                    .apply();

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, UserDashboardActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Invalid USN or Password", Toast.LENGTH_SHORT).show();
        }
    }
}