package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private TextView idFinder;
    private TextView passwordFinder;
    private Intent search;
    private String idFinding = "Id";
    private String passwordFinding = "Password";
    private Intent reset;
    private ProfileController profileController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        registerButton = findViewById(R.id.RegisterButton);
        logInButton = findViewById(R.id.logInButton);
        idFinder = findViewById(R.id.resetID);
        passwordFinder = findViewById(R.id.resetPassword);
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
                            fireStoreController.readProfile(id, new FireStoreController.FireStoreProfileCallback() {
                                @Override
                                public void onCallback(String name, String password, String email, Integer avatar) {
                                    profileController = new ProfileController(name, password, email, id, avatar);
                                    profileController.creator();
                                    GlobalVariable.profile = profileController.getProfile();
                                    search = new Intent(LogInActivity.this, SearchExperimentActivity.class);
                                    startActivity(search);
                                    finish();
                                }
                            }, new FireStoreController.FireStoreProfileFailCallback() {
                                @Override
                                public void onCallback() {
                                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                }
                            });
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

        // Find ID
        idFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finding(idFinding);
            }
        });

        // Find Password
        passwordFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finding(passwordFinding);
            }
        });
    }

    private void finding(String verifier){
        reset = new Intent(LogInActivity.this, Reseter.class);
        reset.putExtra("Verifier", verifier);
        startActivity(reset);
        finish();
    }
}