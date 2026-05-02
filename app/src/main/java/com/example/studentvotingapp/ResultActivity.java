package com.example.studentvotingapp;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class ResultActivity extends AppCompatActivity {

    TableLayout tableLayout;
    SharedPreferences candidateSP, voteSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = findViewById(R.id.resultToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Voting Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = findViewById(R.id.resultTable);
        Button btnDownload = findViewById(R.id.btnDownloadResult);

        candidateSP = getSharedPreferences("Candidates", MODE_PRIVATE);
        voteSP = getSharedPreferences("Votes", MODE_PRIVATE);

        loadResults();

        btnDownload.setOnClickListener(v -> saveTableAsImage());
    }

    private void loadResults() {
        Set<String> candidates =
                candidateSP.getStringSet("list", new HashSet<>());

        // Table Header
        TableRow header = new TableRow(this);
        header.addView(createCell("Candidate Name", true));
        header.addView(createCell("Votes", true));
        tableLayout.addView(header);

        for (String name : candidates) {
            int votes = voteSP.getInt("vote_" + name, 0);

            TableRow row = new TableRow(this);
            row.addView(createCell(name, false));
            row.addView(createCell(String.valueOf(votes), false));

            tableLayout.addView(row);
        }
    }

    private TextView createCell(String text, boolean header) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(20, 20, 20, 20);
        tv.setTextSize(16);
        tv.setBackgroundResource(android.R.drawable.editbox_background);
        if (header) tv.setTextSize(18);
        return tv;
    }

    private void saveTableAsImage() {
        Bitmap bitmap =
                Bitmap.createBitmap(tableLayout.getWidth(),
                        tableLayout.getHeight(),
                        Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        tableLayout.draw(canvas);

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                    "Voting_Result_" + System.currentTimeMillis() + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VotingResults");

            Uri uri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            OutputStream out = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            Toast.makeText(this,
                    "Result downloaded to Gallery",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this,
                    "Download failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
