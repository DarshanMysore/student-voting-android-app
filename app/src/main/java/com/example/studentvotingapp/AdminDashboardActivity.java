package com.example.studentvotingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Set;

public class AdminDashboardActivity extends AppCompatActivity {

    LinearLayout graphContainer;
    ActionBarDrawerToggle toggle; // ✅ GLOBAL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // 🔹 Toolbar
        Toolbar toolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);

        // 🔹 Drawer setup
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 🔹 Drawer menu click
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_home) {
                return true;
            }

            if (id == R.id.menu_manage) {
                startActivity(new Intent(this, AdminManageCandidateActivity.class));
            }

            if (id == R.id.menu_voters) {
                startActivity(new Intent(this, VoterManagementActivity.class));
            }

            if (id == R.id.menu_results) {
                startActivity(new Intent(this, ResultActivity.class));
            }

            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, AdminProfileActivity.class));
            }

            if (id == R.id.menu_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // 🔹 Existing logic
        graphContainer = findViewById(R.id.graphContainer);

        LinearLayout navResult = findViewById(R.id.navResult);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        navResult.setOnClickListener(v ->
                startActivity(new Intent(this, ResultActivity.class))
        );

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProfileActivity.class))
        );

        Button btnReset = findViewById(R.id.btnResetVotes);
        btnReset.setOnClickListener(v -> {
            getSharedPreferences("Votes", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            loadResults();
        });

        loadResults();
    }

    // ✅ THIS FIXES HAMBURGER CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadResults() {

        graphContainer.removeAllViews();

        Set<String> candidates =
                getSharedPreferences("Candidates", MODE_PRIVATE)
                        .getStringSet("list", null);

        if (candidates == null || candidates.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No voting data");
            tv.setGravity(Gravity.CENTER);
            graphContainer.addView(tv);
            return;
        }

        for (String name : candidates) {

            int votes =
                    getSharedPreferences("Votes", MODE_PRIVATE)
                            .getInt("vote_" + name, 0);

            LinearLayout barLayout = new LinearLayout(this);
            barLayout.setOrientation(LinearLayout.VERTICAL);
            barLayout.setGravity(Gravity.BOTTOM);
            barLayout.setPadding(16, 0, 16, 0);

            TextView tvVotes = new TextView(this);
            tvVotes.setText(String.valueOf(votes));
            tvVotes.setGravity(Gravity.CENTER);

            View bar = new View(this);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    80,
                    Math.max(votes * 40, 20)
            ));
            bar.setBackgroundColor(0xFF00E676);

            TextView tvName = new TextView(this);
            tvName.setText(name);
            tvName.setGravity(Gravity.CENTER);

            barLayout.addView(tvVotes);
            barLayout.addView(bar);
            barLayout.addView(tvName);

            graphContainer.addView(barLayout);
        }
    }
}