package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsn, etPassword, etConfirmPassword;
    Spinner spBranch;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etUsn = findViewById(R.id.etUsn);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spBranch = findViewById(R.id.spBranch);
        btnRegister = findViewById(R.id.btnRegister);

        // 🔹 Spinner setup (fixed)
        String[] branches = {"Select Branch", "MCA", "MBA", "BE"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                branches
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(android.R.color.white)); // selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(android.R.color.black)); // dropdown list
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(adapter);
        spBranch.setSelection(0);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String usn = etUsn.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String branch = spBranch.getSelectedItem().toString();

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);

        // 🔴 Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter name");
            return;
        }

        if (TextUtils.isEmpty(usn)) {
            etUsn.setError("Enter USN");
            return;
        }

        if (spBranch.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a branch", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // 🔴 Duplicate check
        if (sp.contains(usn + "_password")) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ SAFE user list handling (fixed)
        Set<String> users = new HashSet<>();
        Set<String> storedUsers = sp.getStringSet("users_list", null);

        if (storedUsers != null) {
            users.addAll(storedUsers);
        }

        users.add(usn);

        // 🔹 Save user data
        sp.edit()
                .putString(usn + "_name", name)
                .putString(usn + "_branch", branch)
                .putString(usn + "_password", password)
                .putStringSet("users_list", users)
                .apply();

        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}