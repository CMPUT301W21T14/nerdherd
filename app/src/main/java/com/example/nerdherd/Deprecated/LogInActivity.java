package com.example.nerdherd.Deprecated;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.R;
import com.example.nerdherd.UserInterfaceSearch.SearchExperimentActivity;

import java.util.ArrayList;

/**
 * Login activity in the app
 * Login is the first thing the user sees despite them being a user or an owner
 * It provides login interface, registration button, login button etc
 * @author Zhipeng Z. zhipeng4
 */

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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String preferencesName = "SharedPreferences";
    private Boolean loggedIn;
    private String loggedInName = "Logged In";
    private String loggedInId = "User Id";
    private String userName = "User Name";
    private String userEmail = "User Email";
    private String userPassword = "User Password";
    private String userAvatar = "User Avatar";
    private String name;
    private String email;
    private Integer avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        registerButton = findViewById(R.id.RegisterButton);
        logInButton = findViewById(R.id.logInButton);
        idFinder = findViewById(R.id.resetID);
        passwordFinder = findViewById(R.id.resetPassword);
        idEdit = findViewById(R.id.userId);
        passwordEdit = findViewById(R.id.userPassword);
        fireStoreController = new FireStoreController();

        sharedPreferences = getSharedPreferences(preferencesName, 0);
        loggedIn = sharedPreferences.getBoolean(loggedInName, false);
        id = sharedPreferences.getString(loggedInId, "");

        if (loggedIn){
            name = sharedPreferences.getString(userName, "");
            password = sharedPreferences.getString(userPassword, "");
            email = sharedPreferences.getString(userEmail, "");
            avatar = sharedPreferences.getInt(userAvatar, -1);
            profileController = new ProfileController(name, password, email, id, avatar);
            profileController.creator();
            GlobalVariable.profile = profileController.getProfile();
            search = new Intent(LogInActivity.this, SearchExperimentActivity.class);
            startActivity(search);
            finish();
        }
        else{
            idEdit.setText(id);
        }

        // Click register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                //finish();
            }
        });

        // Click log in button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indicator = 0;
                infoArray = new ArrayList<Pair>();
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
                            fireStoreController.readProfile(null, id, "Current User", new FireStoreController.FireStoreProfileCallback() {
                                @Override
                                public void onCallback(String name, String password, String email, Integer avatar) {
                                    profileController = new ProfileController(name, password, email, id, avatar);
                                    profileController.creator();
                                    GlobalVariable.profile = profileController.getProfile();

                                    sharedPreferences = LogInActivity.this.getSharedPreferences(preferencesName, 0);
                                    editor = sharedPreferences.edit();
                                    editor.putString(loggedInId, id);
                                    editor.putString(userName, name);
                                    editor.putString(userEmail, email);
                                    editor.putString(userPassword, password);
                                    editor.putInt(userAvatar, avatar);
                                    editor.putBoolean(loggedInName, true);
                                    editor.apply();

                                    search = new Intent(LogInActivity.this, SearchExperimentActivity.class);
                                    startActivity(search);
                                    finish();
                                }
                            }, new FireStoreController.FireStoreProfileFailCallback() {
                                @Override
                                public void onCallback() {
                                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                }
                            },null, null);
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