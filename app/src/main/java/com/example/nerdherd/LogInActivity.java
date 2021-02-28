package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogInActivity extends AppCompatActivity {

    private Button registerButton;
    private Intent registerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        registerButton = findViewById(R.id.RegisterButton);

        // Click register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }
}