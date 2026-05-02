package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvRole, tvInstitute;
    ImageView adminPhoto;
    Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // 🔹 Toolbar
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 🔹 Views
        tvName = findViewById(R.id.tvAdminName);
        tvEmail = findViewById(R.id.tvAdminEmail);
        tvRole = findViewById(R.id.tvAdminRole);
        tvInstitute = findViewById(R.id.tvInstitute);
        adminPhoto = findViewById(R.id.adminPhoto);
        btnEdit = findViewById(R.id.btnEditProfile);

        adminPhoto.setImageResource(R.drawable.nobg);

        // 🔹 Load data
        loadAdminData();

        // 🔹 Edit button
        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditAdminProfileActivity.class))
        );
    }

    // 🔹 Load admin data
    private void loadAdminData() {
        SharedPreferences sp = getSharedPreferences("AdminData", MODE_PRIVATE);

        String username = sp.getString("admin_username", "admin");
        String email = sp.getString("admin_email", "admin@mitm.edu");

        tvName.setText(username);
        tvEmail.setText(email);
        tvRole.setText("Administrator");
        tvInstitute.setText("MITM College");

        // 🔹 Load profile image
        String imageBase64 = sp.getString("admin_image", null);

        if (imageBase64 != null) {
            byte[] decoded = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            adminPhoto.setImageBitmap(bitmap);
        } else {
            adminPhoto.setImageResource(R.drawable.nobg);
        }
    }

    // 🔹 Refresh when coming back from edit screen
    @Override
    protected void onResume() {
        super.onResume();
        loadAdminData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}