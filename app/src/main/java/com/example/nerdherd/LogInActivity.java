package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {

    private Button registerButton;
    private Button logInButton;
    private Intent registerIntent;
    private String id;
    private String password;
    private TextView idEdit;
    private TextView passwordEdit;
    private Pair<String, String> logInPair;
    private Integer indicator;
    private ArrayList<Pair> infoArray;
    private FireStoreController fireStoreController;
    private Intent search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        registerButton = findViewById(R.id.RegisterButton);
        logInButton = findViewById(R.id.logInButton);
        fireStoreController = new FireStoreController();

        // Click register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        // Click log in button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indicator = 0;
                infoArray = new ArrayList<Pair>();
                idEdit = findViewById(R.id.userId);
                passwordEdit = findViewById(R.id.userPassword);
                id = idEdit.getText().toString();
                password = passwordEdit.getText().toString();
                logInPair = new Pair<String, String>(id, password);
                fireStoreController.logInCheck(infoArray, new FireStoreController.FireStoreCheckCallback() {
                    @Override
                    public void onCallback(ArrayList<Pair> list) {
                        for (Pair<String, String> infoPair : list){
                            if (infoPair.equals(logInPair)){
                                indicator = 1;
                            }
                        }
                        if (indicator == 1){
                            search = new Intent(LogInActivity.this, SearchExperimentActivity.class);
                            startActivity(search);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "The log in information is not correct, please try again. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new FireStoreController.FireStoreCheckFailCallback() {
                    @Override
                    public void onCallback() {
                        Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}