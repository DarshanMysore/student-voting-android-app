package com.example.studentvotingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

public class VoterManagementActivity extends AppCompatActivity {

    LinearLayout voterContainer;
    Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_management);

        voterContainer = findViewById(R.id.voterContainer);
        btnDownload = findViewById(R.id.btnDownload);

        loadVoters();

        // ✅ Download button
        btnDownload.setOnClickListener(v -> exportAsImage());
    }

    private void loadVoters() {

        voterContainer.removeAllViews();

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);

        Set<String> users = sp.getStringSet("users_list", new HashSet<>());

        if (users == null || users.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No users found");
            tv.setGravity(Gravity.CENTER);
            voterContainer.addView(tv);
            return;
        }

        for (String usn : users) {

            String name = sp.getString(usn + "_name", "N/A");
            String branch = sp.getString(usn + "_branch", "N/A");

            // ✅ Card layout
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(30, 20, 30, 20);
            card.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20);
            card.setLayoutParams(params);

            // ✅ User info
            TextView tvInfo = new TextView(this);
            tvInfo.setText("👤 " + name +
                    "\n🆔 " + usn +
                    "\n🎓 " + branch);
            tvInfo.setTextSize(16);

            // ✅ Delete button
            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");
            btnDelete.setBackgroundColor(0xFFD32F2F);
            btnDelete.setTextColor(0xFFFFFFFF);

            btnDelete.setOnClickListener(v -> {

                // 🔥 FIX: always get fresh list
                Set<String> updatedUsers =
                        new HashSet<>(sp.getStringSet("users_list", new HashSet<>()));

                updatedUsers.remove(usn);

                sp.edit()
                        .remove(usn + "_name")
                        .remove(usn + "_branch")
                        .remove(usn + "_password")
                        .remove("hasVoted_" + usn) // also remove vote flag
                        .putStringSet("users_list", updatedUsers)
                        .apply();

                Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show();

                loadVoters(); // refresh UI
            });

            card.addView(tvInfo);
            card.addView(btnDelete);

            voterContainer.addView(card);
        }
    }

    // ✅ CSV Download Feature
    private void exportAsImage() {

        try {
            SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
            Set<String> users = sp.getStringSet("users_list", new HashSet<>());

            if (users == null || users.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create text content
            StringBuilder text = new StringBuilder();
            text.append("VOTER LIST\n\n");

            for (String usn : users) {
                String name = sp.getString(usn + "_name", "");
                String branch = sp.getString(usn + "_branch", "");

                text.append("Name: ").append(name).append("\n");
                text.append("USN: ").append(usn).append("\n");
                text.append("Branch: ").append(branch).append("\n\n");
            }

            // Create bitmap
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setTextSize(40);
            paint.setColor(android.graphics.Color.BLACK);

            int width = 1000;
            int height = (users.size() + 3) * 120;

            android.graphics.Bitmap bitmap =
                    android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);

            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            canvas.drawColor(android.graphics.Color.WHITE);

            int x = 40;
            int y = 80;

            for (String line : text.toString().split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += 80;
            }

            // Save to gallery
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "voter_list.png");
            values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StudentVoting");

            android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
            );

            java.io.OutputStream out = getContentResolver().openOutputStream(uri);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            Toast.makeText(this, "Saved to Gallery ✅", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}