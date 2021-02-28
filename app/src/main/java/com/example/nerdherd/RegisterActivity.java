package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Random;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private String existed;
    private ArrayList<String> emailData;
    private Boolean isNew;
    private ArrayList<String> idData;

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
                isNew = true;
                idGenerator();
                textGetter();
                if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                    emailData = new ArrayList<String>();
                    readData(emailData, "Email", new FireStoreCallback() {
                        @Override
                        public void onCallback(ArrayList<String> list) {
                            for(String usedEmail : list){
                                if (usedEmail.equals(email)){
                                    isNew = false;
                                }
                            }
                            if (isNew){
                                profileController = new ProfileController(name, password, email, id, avatar);
                                profileController.creator();
                                profileController.uploadProfile();
                                searchIntent = new Intent(RegisterActivity.this, SearchExperimentActivity.class);
                                startActivity(searchIntent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "The email address is already used. Thank you.", Toast.LENGTH_SHORT).show();
                            }
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
        readData(idData, "Id", new FireStoreCallback() {
            @Override
            public void onCallback(ArrayList<String> list) {
                for(String usedId : idData){
                    if (usedId.equals(idSequence)){
                        indicator = 0;
                    }
                }
                if (indicator == 0){
                    idGenerator();
                }
                else{
                    id = idSequence;
                }
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

    private void readData(ArrayList<String> itemList, String verifier, FireStoreCallback fireStoreCallback){
        itemList.clear();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Profile");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        if (verifier.equals("Email")) {
                            existed = doc.getString("Email");
                        }
                        if (verifier.equals("Id")){
                            existed = doc.getId();
                        }
                        itemList.add(existed);
                    }
                    fireStoreCallback.onCallback(itemList);
                }
                else{
                    Log.d("Error", task.getException().toString());
                }
            }
        });
    }

    private interface FireStoreCallback{
        void onCallback(ArrayList<String> list);
    }
}