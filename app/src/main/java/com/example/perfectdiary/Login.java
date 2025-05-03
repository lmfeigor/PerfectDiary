package com.example.perfectdiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    private DatabaseHelper databaseHelper;

    @Override
    protected void  onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        String username = editTextUsername.getText().toString().trim();
        databaseHelper = new DatabaseHelper (this, username);

        //Set onClickListener for login button
        buttonLogin.setOnClickListener(v -> loginUser());

        //Set onClickListener for signup text
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);
        });
    }

    //Initialize all views
    private void initViews () {
        editTextUsername = findViewById(R.id.Username);
        editTextPassword = findViewById(R.id.Password);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
    }

    //Validate user input and perform login
    private void loginUser() {
        //Get values
        String username = editTextUsername.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextUsername.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        // Check user exists and password matches
        if (databaseHelper.checkUserLogin(username, password)) {
            // Create/initialize user-specific database
            DatabaseHelper userDbHelper = new DatabaseHelper(this, username);

            // Store the login status and username in shared preferences
            saveLoginStatus(username);

            // Pass the database helper to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
    //Save login status to shared preferences
    private void saveLoginStatus(String username) {
        getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", true)
                .putString("loggedInUser", username)
                .apply();
    }
}
