package com.example.nerdherd;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Register for the app and how the user sees it on the app
 * @author Zhipeng Z. zhipeng4
 */

public class RegisterActivity extends Activity {

    private Button cancelButton;
    private Button registerButton;
    private ImageView avatarButton;
    private Intent logInIntent;
    private Intent searchIntent;
    private String name;
    private String password;
    public  String email;
    private String id;
    private Integer avatar;
    private TextView nameEdit;
    private TextView passwordEdit;
    private TextView emailEdit;
    private ProfileController profileController;
    private Integer randInt;
    private String randSequence;
    private Random random;
    private String idSequence;
    private ProfileController emptyController;
    private Integer indicator;
    private Intent avatarPicker;
    private Intent getAvatar;
    private Bundle dataBundle;
    private ArrayList<String> emailData;
    private Boolean isNew;
    private ArrayList<String> idData;
    private FireStoreController fireStoreController;
    private GMailSender GMailSender;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String preferencesName = "SharedPreferences";
    private String loggedInName = "Logged In";
    private String loggedInId = "User Id";
    private String userName = "User Name";
    private String userEmail = "User Email";
    private String userPassword = "User Password";
    private String userAvatar = "User Avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fireStoreController = new FireStoreController();
        avatar = 0;
        getAvatar = getIntent();
        dataBundle = getAvatar.getBundleExtra("Data");
        if (dataBundle != null) {
            avatar = dataBundle.getInt("Index", 0);
            name = dataBundle.getString("Name");
            password = dataBundle.getString("Password");
            email = dataBundle.getString("Email");
        }
        emptyController = new ProfileController();

        cancelButton = findViewById(R.id.cancelButton);
        registerButton = findViewById(R.id.registerButton);
        avatarButton = findViewById(R.id.avatarEdit);
        nameEdit = findViewById(R.id.nameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        emailEdit = findViewById(R.id.emailEdit);

        nameEdit.setText(name);
        passwordEdit.setText(password);
        emailEdit.setText(email);

        avatarButton.setImageResource(emptyController.getImageArray().get(avatar));

        // Click cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInIntent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(logInIntent);
                finish();
            }
        });

        // Click register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = true;
                idGenerator();
                textGetter();
                if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                    emailData = new ArrayList<String>();
                    fireStoreController.readData(emailData, "Email", new FireStoreController.FireStoreReadCallback() {
                        @Override
                        public void onCallback(ArrayList<String> list) {
                            for (String usedEmail : list) {
                                if (usedEmail.equals(email)) {
                                    isNew = false;
                                }
                            }
                            if (isNew) {
                                profileController = new ProfileController(name, password, email, id, avatar);
                                profileController.creator();
                                GlobalVariable.profile = profileController.getProfile();
                                fireStoreController.uploadData(profileController.getProfile(), id, new FireStoreController.FireStoreUploadCallback() {
                                    @Override
                                    public void onCallback() {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    GMailSender = new GMailSender("richard15765@gmail.com", "ZhiPeng4!");
                                                    GMailSender.sendMail("Your User ID", "Your user ID is: " + id + "\nPlease do not forget.", "richard15765@gmail.com", email);
                                                }catch(Exception e){
                                                    Log.e("SendMail", e.getMessage(), e);
                                                }
                                            }
                                        }).start();

                                        sharedPreferences = RegisterActivity.this.getSharedPreferences(preferencesName, 0);
                                        editor = sharedPreferences.edit();
                                        editor.putString(loggedInId, id);
                                        editor.putString(userName, name);
                                        editor.putString(userEmail, email);
                                        editor.putString(userPassword, password);
                                        editor.putInt(userAvatar, avatar);
                                        editor.putBoolean(loggedInName, true);
                                        editor.apply();

                                        searchIntent = new Intent(RegisterActivity.this, SearchExperimentActivity.class);
                                        startActivity(searchIntent);
                                        finish();
                                    }
                                }, new FireStoreController.FireStoreUploadFailCallback() {
                                    @Override
                                    public void onCallback() {
                                        Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "The email address is already used. Thank you.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new FireStoreController.FireStoreReadFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please fill all of name, password and email. Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Click avatar button
        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textGetter();
                dataBundle = new Bundle();
                dataBundle.putString("Name", name);
                dataBundle.putString("Password", password);
                dataBundle.putString("Email", email);
                avatarPicker = new Intent(RegisterActivity.this, AvatarPicker.class);
                avatarPicker.putExtra("Data", dataBundle);
                startActivity(avatarPicker);
                finish();
            }
        });
    }

    private void idGenerator(){
        indicator = 1;
        idSequence = generator();
        idData = new ArrayList<String>();
        fireStoreController.readData(idData, "Id", new FireStoreController.FireStoreReadCallback() {
            @Override
            public void onCallback(ArrayList<String> list) {
                for (String usedId : idData) {
                    if (usedId.equals(idSequence)) {
                        indicator = 0;
                    }
                }
                if (indicator == 0) {
                    idGenerator();
                } else {
                    id = idSequence;
                }
            }
        }, new FireStoreController.FireStoreReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please tray again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generator(){
        randSequence = "1";
        random = new Random();
        while (randSequence.length() < 10) {
            randInt = random.nextInt(10);
            randSequence = randSequence + randInt.toString();
        }
        return randSequence;
    }

    private void textGetter(){
        name = nameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        email = emailEdit.getText().toString();
    }
}