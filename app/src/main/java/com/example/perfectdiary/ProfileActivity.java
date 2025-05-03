package com.example.perfectdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate; // Import for setting night mode
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;
    private TextView usernameTextView;
    private TextView themeTextView;
    private LinearLayout usernameLayout;
    private LinearLayout changePasswordLayout;
    private LinearLayout themeLayout;
    private Button clearAllEntriesButton;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        // Initialize views
        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        themeTextView = findViewById(R.id.themeTextView);
        usernameLayout = findViewById(R.id.usernameLayout);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        themeLayout = findViewById(R.id.themeLayout);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize SharedPreferences and DatabaseHelper
        sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE); // Corrected shared preferences name
        String currentUsername = sharedPreferences.getString("loggedInUser", "default_user");
        dbHelper = new DatabaseHelper(this,currentUsername);

        // Load and display current user data
        loadUserData();

        // Set up click listeners for profile settings
        usernameLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangeUsernameActivity.class);
            startActivity(intent);
        });

        changePasswordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        themeLayout.setOnClickListener(v -> {
            String currentTheme = sharedPreferences.getString("theme", "Light");
            String newTheme = currentTheme.equals("Light") ? "Dark" : "Light";
            saveTheme(newTheme);
            applyTheme(newTheme);
            themeTextView.setText(newTheme);
            // recreate(); // Removed recreate() and using AppCompatDelegate
        });


        logoutButton.setOnClickListener(v -> {
            Log.d("ProfileActivity", "Logout button clicked");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Set up Bottom Navigation View listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;

                switch (item.getOrder()) {

                    case 0: // Home


                        intent = new Intent(ProfileActivity.this, MainActivity.class);

                        startActivity(intent);

                        return true;

                    case 1: // Search

                        intent = new Intent(ProfileActivity.this, SearchActivity.class);

                        startActivity(intent);

                        return true;

                    case 2: // Add

                        intent = new Intent(ProfileActivity.this, Add.class);

                        startActivity(intent);

                        return true;

                    case 3: // Emotion

                        intent = new Intent(ProfileActivity.this, EmotionalAnalysisActivity.class);

                        startActivity(intent);

                        return true;

                    case 4: // Profile


                        return true;

                }

                return false;

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        String username = sharedPreferences.getString("loggedInUser", "User");
        String theme = sharedPreferences.getString("theme", "Light");
        Log.d("ProfileActivity", "Loaded username from SharedPreferences: " + username);
        usernameTextView.setText(username);
        themeTextView.setText(theme);
        applyTheme(theme);
    }

    private void saveTheme(String newTheme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", newTheme);
        editor.apply();
    }

    private void applyTheme(String theme) {
        Log.d("ProfileActivity", "Applying theme: " + theme); // Added logging
        if (theme.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

