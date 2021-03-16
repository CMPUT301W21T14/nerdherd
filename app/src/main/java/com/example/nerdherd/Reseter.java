package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Random;

public class Reseter extends AppCompatActivity {

    private TextView emailView;
    private Button cancelButton;
    private Button finishButton;
    private String userEmail;
    private Intent logIntent;
    private FireStoreController fireStoreController;
    private ArrayList<String> rightEmail;
    private Integer indicator;
    private String verifier;
    private Intent previous;
    private String title;
    private String message;
    private ArrayList<String> targetList;
    private GMailSender GMailSender;
    private Random random;
    private String randSequence;
    private Integer randInt;
    private String randPassword;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseter);

        previous = getIntent();
        verifier = previous.getStringExtra("Verifier");

        emailView = findViewById(R.id.emailEdit2);
        cancelButton = findViewById(R.id.resetCancel);
        finishButton = findViewById(R.id.resetFinish);
        fireStoreController = new FireStoreController();
        rightEmail = new ArrayList<String>();

        // Click cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIntent = new Intent(Reseter.this, LogInActivity.class);
                startActivity(logIntent);
                finish();
            }
        });

        // Click finish
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = emailView.getText().toString();
                if(!userEmail.isEmpty()){
                    indicator = 0;
                    fireStoreController.readData(rightEmail, "Email", new FireStoreController.FireStoreReadCallback() {
                        @Override
                        public void onCallback(ArrayList<String> list) {
                            for (String realEmail : list) {
                                if (realEmail.equals(userEmail)){
                                    indicator = 1;
                                }
                            }
                            if (indicator == 1){
                                targetList = new ArrayList<String>();
                                fireStoreController.getCertainData(targetList, "Email", userEmail, "Id", new FireStoreController.FireStoreCertainCallback() {
                                    @Override
                                    public void onCallback(ArrayList<String> list) {
                                        docId = list.get(0);
                                        if (verifier.equals("Id")) {
                                            title = "Your User ID";
                                            message = "Your user ID is: " + docId + "\nPlease do not forgot.";
                                            sender(title, message, userEmail);
                                        }
                                        if (verifier.equals("Password")){
                                            randPassword = generator();
                                            fireStoreController.updater("Profile", docId, "Password", randPassword, new FireStoreController.FireStoreUpdateCallback() {
                                                @Override
                                                public void onCallback() {
                                                    title = "Your new password";
                                                    message = "Your new password is: " + randPassword + "\nPlease change it ASAP after log in. Thank you.";
                                                    sender(title, message, userEmail);
                                                }
                                            }, new FireStoreController.FireStoreUpdateFailCallback() {
                                                @Override
                                                public void onCallback() {
                                                    Toast.makeText(getApplicationContext(), "Cannot connect the database. Please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }, new FireStoreController.FireStoreCertainFailCallback() {
                                    @Override
                                    public void onCallback() {
                                        Toast.makeText(getApplicationContext(), "Cannot connect the database. Please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }, new FireStoreController.FireStoreReadFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "Cannot connect the database. Please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter your email. Thank you.", Toast.LENGTH_SHORT).show();
                }
                new AlertDialog.Builder(Reseter.this).setTitle("Email Sent").setMessage("The Email is sent. Please check your inbox. Thank you.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logIntent = new Intent(Reseter.this, LogInActivity.class);
                                startActivity(logIntent);
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void sender(String subject, String body, String target){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender = new GMailSender("richard15765@gmail.com", "ZhiPeng4!");
                    GMailSender.sendMail(subject, body, "richard15765@gmail.com", target);
                }catch(Exception e){
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }

    private String generator(){
        randSequence = "";
        random = new Random();
        while (randSequence.length() < 6) {
            randInt = random.nextInt(10);
            randSequence = randSequence + randInt.toString();
        }
        return randSequence;
    }
}