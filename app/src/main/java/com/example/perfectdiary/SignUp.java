package com.example.perfectdiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignUp;
    private DatabaseHelper databaseHelper;

    @Override
    protected void  onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews(); //Initialize views
        String username = editTextUsername.getText().toString().trim();
        databaseHelper = new DatabaseHelper (this, username);

        //Set onClickListener for signup button
        buttonSignUp.setOnClickListener(v -> registerUser());
    }
    private void initViews() {
        editTextUsername = findViewById(R.id.Username);
        editTextPassword = findViewById(R.id.Password);
        editTextConfirmPassword = findViewById(R.id.ConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
    }

    //Validate user inputs and register user
    private void registerUser(){
        String username = editTextUsername.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)){
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            editTextConfirmPassword.setError("Please confirm your password");
            editTextConfirmPassword.requestFocus();
            return;
        }
         if(!password.equals(confirmPassword)){
             editTextConfirmPassword.setError("Password do not match");
             editTextConfirmPassword.requestFocus();
             return;
         }
         if(databaseHelper.checkUser(username)){
             editTextUsername.setError("Username already exists");
             editTextUsername.requestFocus();
             return;
         }

         //add user to database
        if (databaseHelper.addUser(username,password)){
            // Create the user's database immediately after registration
            DatabaseHelper userDbHelper = new DatabaseHelper(this, username);
            userDbHelper.close(); // Just creates the database file

            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }, 1000);
        }
        else {
            Toast.makeText(this, "Registration Failed. Please Try Again.", Toast.LENGTH_SHORT).show();
        }
    }
}
