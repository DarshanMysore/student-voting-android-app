package com.example.studentvotingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class AdminManageCandidateActivity extends AppCompatActivity {

    LinearLayout container;
    EditText etName;
    SharedPreferences candidateSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_candidate);

        etName = findViewById(R.id.etCandidateName);
        Button btnAdd = findViewById(R.id.btnAddCandidate);
        container = findViewById(R.id.candidateContainer);

        candidateSP = getSharedPreferences("Candidates", MODE_PRIVATE);

        loadCandidates();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Enter candidate name");
                return;
            }

            Set<String> candidates = new HashSet<>(
                    candidateSP.getStringSet("list", new HashSet<>())
            );

            if (candidates.contains(name)) {
                Toast.makeText(this, "Candidate already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            candidates.add(name);
            candidateSP.edit().putStringSet("list", candidates).apply();

            etName.setText("");
            loadCandidates();
            Toast.makeText(this, "Candidate added", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCandidates() {

        container.removeAllViews();

        Set<String> candidates = new HashSet<>(
                candidateSP.getStringSet("list", new HashSet<>())
        );

        if (candidates.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No candidates added yet");
            tv.setGravity(Gravity.CENTER);
            container.addView(tv);
            return;
        }

        for (String name : candidates) {

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8, 8, 8, 8);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setText(name);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));

            Button btnDelete = new Button(this);
            btnDelete.setText("Delete");

            btnDelete.setOnClickListener(v -> {
                Set<String> updated = new HashSet<>(
                        candidateSP.getStringSet("list", new HashSet<>())
                );
                updated.remove(name);
                candidateSP.edit().putStringSet("list", updated).apply();
                loadCandidates();
                Toast.makeText(this, "Candidate deleted", Toast.LENGTH_SHORT).show();
            });

            row.addView(tvName);
            row.addView(btnDelete);

            container.addView(row);
        }
    }
}
