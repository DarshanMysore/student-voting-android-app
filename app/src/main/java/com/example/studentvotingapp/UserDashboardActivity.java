package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserDashboardActivity extends AppCompatActivity {

    TextView tvName, tvUsn, tvBranch;
    Button btnVote, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // 🔹 UI mapping
        tvName = findViewById(R.id.tvName);
        tvUsn = findViewById(R.id.tvUsn);
        tvBranch = findViewById(R.id.tvBranch);
        btnVote = findViewById(R.id.btnVote);
        btnLogout = findViewById(R.id.btnLogout);

        // 🔹 SharedPreferences
        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);

        String usn = sp.getString("currentUser", "");
        String name = sp.getString("name", "N/A");
        String branch = sp.getString("branch", "N/A");

        // 🔹 Display data
        tvName.setText("Name : " + name);
        tvUsn.setText("USN : " + usn);
        tvBranch.setText("Branch : " + branch);

        // 🔹 Check voting status
        String voteKey = "hasVoted_" + usn;
        boolean hasVoted = sp.getBoolean(voteKey, false);

        if (hasVoted) {
            btnVote.setEnabled(false);
            btnVote.setText("Vote Completed");
        }

        // 🔹 Vote button
        btnVote.setOnClickListener(v -> {
            if (!hasVoted) {
                startActivity(new Intent(this, CandidateActivity.class));
            } else {
                Toast.makeText(this, "You have already voted", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Logout button
        btnLogout.setOnClickListener(v -> {
            sp.edit().clear().apply(); // clear session

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}