package com.example.studentvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class CandidateActivity extends AppCompatActivity {

    LinearLayout candidateContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        candidateContainer = findViewById(R.id.candidateContainer);

        // 🔹 Logged-in user
        SharedPreferences userSP = getSharedPreferences("UserData", MODE_PRIVATE);
        String usn = userSP.getString("currentUser", "");

        if (usn == null || usn.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Already voted check
        if (userSP.getBoolean("hasVoted_" + usn, false)) {
            Toast.makeText(this, "You have already voted", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 🔹 Load candidates
        SharedPreferences candidateSP = getSharedPreferences("Candidates", MODE_PRIVATE);
        Set<String> candidates =
                candidateSP.getStringSet("list", new HashSet<>());

        if (candidates.isEmpty()) {
            Toast.makeText(this, "No candidates available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Create CARD UI for each candidate
        for (String candidate : candidates) {

            // 🟪 Card container
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setPadding(20, 20, 20, 20);
            card.setBackgroundColor(0xFFFFFFFF);
            card.setElevation(6);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 20);
            card.setLayoutParams(cardParams);

            // 👤 Profile image
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.pro2);
            LinearLayout.LayoutParams imgParams =
                    new LinearLayout.LayoutParams(120, 120);
            image.setLayoutParams(imgParams);

            // 📄 Info section
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            infoLayout.setPadding(20, 0, 0, 0);
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));

            TextView tvName = new TextView(this);
            tvName.setText(candidate);
            tvName.setTextSize(16);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvBranch = new TextView(this);
            tvBranch.setText("Branch: MCA"); // 🔹 static for now
            tvBranch.setTextSize(14);

            infoLayout.addView(tvName);
            infoLayout.addView(tvBranch);

            // 🗳 Vote button
            Button btnVote = new Button(this);
            btnVote.setText("Vote");
            btnVote.setAllCaps(false);
            btnVote.setTextColor(0xFFFFFFFF);
            btnVote.setBackgroundColor(0xFF673AB7);

            LinearLayout.LayoutParams btnParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            btnParams.gravity = Gravity.CENTER_VERTICAL;
            btnVote.setLayoutParams(btnParams);

            btnVote.setOnClickListener(v -> castVote(usn, candidate));

            // 🔹 Add views
            card.addView(image);
            card.addView(infoLayout);
            card.addView(btnVote);

            candidateContainer.addView(card);
        }
    }

    private void castVote(String usn, String candidateName) {

        SharedPreferences voteSP = getSharedPreferences("Votes", MODE_PRIVATE);
        int currentVotes = voteSP.getInt("vote_" + candidateName, 0);

        voteSP.edit()
                .putInt("vote_" + candidateName, currentVotes + 1)
                .apply();

        SharedPreferences userSP = getSharedPreferences("UserData", MODE_PRIVATE);
        userSP.edit()
                .putBoolean("hasVoted_" + usn, true)
                .apply();

        Toast.makeText(
                this,
                "Vote submitted successfully",
                Toast.LENGTH_SHORT
        ).show();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 1000);
    }
}