package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash screen for the app
 * The constant screen that is played for a fixed amount of time when the app is opened
 * @author Zhipeng Z. zhipeng4
 */

public class SplashScreen extends AppCompatActivity {

    private Intent logInIntent;
    private Integer time = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logInIntent = new Intent(SplashScreen.this, LogInActivity.class);
                startActivity(logInIntent);
                finish();
            }
        }, time);
    }

}