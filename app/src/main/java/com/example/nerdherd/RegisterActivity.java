package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity {

    private Button cancelButton;
    private Button registerButton;
    private ImageView avatarButton;
    private Intent logInIntent;
    private Intent searchIntent;
    private String name;
    private String password;
    private String email;
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
    private Integer index;
    private ProfileController emptyController;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private Integer indicator;
    private String existedID;
    private Intent avatarPicker;
    private Intent getAvatar;
    private Integer requestNum = 1;
    private Bundle dataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            }
        });

        // Click register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idGenerator();
                textGetter();
                if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                    profileController = new ProfileController(name, password, email, id, avatar);
                    profileController.creator();
                    profileController.uploadProfile();
                    searchIntent = new Intent(RegisterActivity.this, SearchExperimentActivity.class);
                    startActivity(searchIntent);
                    finish();
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

    private String idGenerator(){
        idSequence = generator();
        while (!checker(idSequence)){
            idSequence = generator();
        }
        return idSequence;
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

    private Boolean checker(String sequence){
        indicator = 1;
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Profile");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value){
                    existedID = doc.getId();
                    if (existedID.equals(sequence)){
                        indicator = 0;
                    }
                }
            }
        });

        if (indicator == 1) {
            return true;
        }
        else{
            return false;
        }
    }

    private void textGetter(){
        name = nameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        email = emailEdit.getText().toString();
    }

}