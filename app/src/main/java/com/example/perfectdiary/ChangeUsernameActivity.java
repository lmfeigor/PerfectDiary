package com.example.perfectdiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeUsernameActivity extends AppCompatActivity {

    private EditText editTextNewUsername;
    private Button buttonSaveUsername;
    private Button buttonCancelUsername;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        buttonSaveUsername = findViewById(R.id.buttonSaveUsername);
        buttonCancelUsername = findViewById(R.id.buttonCancelUsername);
        sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("loggedInUser", "");
        dbHelper = new DatabaseHelper(this,currentUsername);

        buttonSaveUsername.setOnClickListener(v -> saveNewUsername());
        buttonCancelUsername.setOnClickListener(v -> finish()); // Go back to ProfileActivity
    }

    private void saveNewUsername() {
        String newUsername = editTextNewUsername.getText().toString().trim().toLowerCase();
        String currentUsername = sharedPreferences.getString("loggedInUser", "");

        if (TextUtils.isEmpty(newUsername)) {
            editTextNewUsername.setError("Username cannot be empty");
            editTextNewUsername.requestFocus();
            return;
        }

        if (dbHelper.updateUsername(currentUsername, newUsername)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("loggedInUser", newUsername);
            editor.apply();
            Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Go back to ProfileActivity
        } else {
            Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show();
        }
    }
}