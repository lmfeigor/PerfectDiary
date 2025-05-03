package com.example.perfectdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new  Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            finish();
        }, 5000); //Splash activity will be shown for 5 seconds once the app is launched
    }
}
