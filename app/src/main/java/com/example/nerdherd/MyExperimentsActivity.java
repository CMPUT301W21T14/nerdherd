package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MyExperimentsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> returneditems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experiments);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_myExperiment);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(MyExperimentsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();

        getDescription();
        //i own experiements given that iam the owner
        //can use firestore to get all the experiments and return experiment where the users id
        //equals the userid in firestore

    }

    public void getDescription() {
        savedList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        returneditems = new ArrayList<Experiment>();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {


                for (Experiment allExperiment:experiments) {
                    if (allExperiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())) {
                        Log.d("exp description",allExperiment.getDescription());
                        savedList.add(allExperiment);
                        returneditems.add(allExperiment);
                    }
                }
            }
        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });

//        Log.d("my experiment", returneditems.get(1).getDescription());
//        return returneditems.get(1).getDescription();
    }



}