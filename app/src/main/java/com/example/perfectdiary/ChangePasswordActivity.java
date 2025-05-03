package com.example.perfectdiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;
    private Button buttonSavePassword;
    private Button buttonCancelPassword;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonSavePassword = findViewById(R.id.buttonSavePassword);
        buttonCancelPassword = findViewById(R.id.buttonCancelPassword);
        sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("loggedInUser", "");
        dbHelper = new DatabaseHelper(this, currentUsername);

        buttonSavePassword.setOnClickListener(v -> saveNewPassword());
        buttonCancelPassword.setOnClickListener(v -> finish()); // Go back to ProfileActivity
    }

    private void saveNewPassword() {
        String oldPassword = editTextOldPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();
        String currentUsername = sharedPreferences.getString("loggedInUser", "");

        if (TextUtils.isEmpty(oldPassword)) {
            editTextOldPassword.setError("Please enter old password");
            editTextOldPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            editTextNewPassword.setError("Please enter new password");
            editTextNewPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            editTextConfirmNewPassword.setError("Please confirm new password");
            editTextConfirmNewPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            editTextConfirmNewPassword.setError("Passwords do not match");
            editTextConfirmNewPassword.requestFocus();
            return;
        }

        if (dbHelper.updatePassword(currentUsername, oldPassword, newPassword)) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Go back to ProfileActivity
        } else {
            Toast.makeText(this, "Failed to update password. Check old password.", Toast.LENGTH_SHORT).show();
        }
    }
}