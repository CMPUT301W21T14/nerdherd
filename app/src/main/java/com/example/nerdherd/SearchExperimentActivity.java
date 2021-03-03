package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchExperimentActivity extends AppCompatActivity {

    String UserName;
    private Button userprof;
    private TextView usersname;
    private FireStoreController fireStoreController;

    private String id;
    private ArrayList<String> return_val;
    private Intent previousintent;
    private ArrayList<String> getdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experiment);
        previousintent = getIntent();
        fireStoreController = new FireStoreController();
        id  = previousintent.getStringExtra("id");
        Log.d("asd", id);
        ArrayList<String>test = new ArrayList<>();
        fireStoreController.getCertainData(test, "Id", id, "Email", new FireStoreController.FireStoreCertainCallback() {
            @Override
            public void onCallback(ArrayList<String> list) {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                for (String data: list){
                    Log.d("data_vallll", data);
                }
            }
        }, new FireStoreController.FireStoreCertainFailCallback(){
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
//        fireStoreController.getCertainData();
//        email = "email";
//        Log.d("user email", LogInActivity.);
//        infoArray = new ArrayList<Pair>();
//        //dispaly key-value pair from firestore
//        fireStoreController.getCertainData(infoArray, new FireStoreController.FireStoreCheckCallback() {
//            @Override
//            public void onCallback(ArrayList<Pair> list) {
//
//            }
//        };
//        ProfileController c = new ProfileController();
//        String b = c.getProfile().getName();

////        lcontroller = new ProfileController(SearchExperimentActivity.this);
//        //link to xml component
//        userprof = findViewById(R.id.profileButton);
//        usersname = findViewById(R.id.Usersnname);
//        usersname.setText(UserName+"");
//        userprof.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(SearchExperimentActivity.this, )
//            }
//        });

    }
}