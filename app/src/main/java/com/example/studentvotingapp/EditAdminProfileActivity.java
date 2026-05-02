package com.example.studentvotingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditAdminProfileActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnSave;
    ImageView imgProfile;

    Uri imageUri = null;

    private static final int PICK_IMAGE = 1;
    private static final int CAMERA_IMAGE = 2;
    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);

        etUsername = findViewById(R.id.etAdminUsername);
        etEmail = findViewById(R.id.etAdminEmail);
        etPassword = findViewById(R.id.etAdminPassword);
        btnSave = findViewById(R.id.btnSave);
        imgProfile = findViewById(R.id.imgProfile);

        SharedPreferences sp = getSharedPreferences("AdminData", MODE_PRIVATE);

        // 🔹 Load data
        etUsername.setText(sp.getString("admin_username", "admin"));
        etEmail.setText(sp.getString("admin_email", "admin@mitm.edu"));
        etPassword.setText(sp.getString("admin_password", "admin123"));

        // 🔹 Load saved image
        String imageBase64 = sp.getString("admin_image", null);
        if (imageBase64 != null) {
            byte[] decoded = Base64.decode(imageBase64, Base64.DEFAULT);
            imgProfile.setImageBitmap(
                    android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.length)
            );
        }

        // 🔹 Click image → check permission → open picker
        imgProfile.setOnClickListener(v -> checkPermissionAndOpenPicker());

        btnSave.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                etUsername.setError("Enter username");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter password");
                return;
            }

            SharedPreferences.Editor editor = sp.edit();

            editor.putString("admin_username", username);
            editor.putString("admin_email", email);
            editor.putString("admin_password", password);

            // 🔹 Save image
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    editor.putString("admin_image", encodeImage(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            editor.apply();

            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // 🔹 Permission check
    private void checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        PERMISSION_CODE);
            } else {
                showImagePicker();
            }
        } else {
            showImagePicker();
        }
    }

    // 🔹 Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            showImagePicker();
        }
    }

    // 🔹 Show Camera / Gallery dialog
    private void showImagePicker() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_IMAGE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_IMAGE);
                    }

                }).show();
    }

    // 🔹 Handle result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                imgProfile.setImageURI(imageUri);
            }

            if (requestCode == CAMERA_IMAGE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(photo);
                imageUri = getImageUri(photo);
            }
        }
    }

    // 🔹 Encode image
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // 🔹 Bitmap → URI
    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(), bitmap, "Profile", null);
        return Uri.parse(path);
    }
}