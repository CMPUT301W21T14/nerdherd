package com.example.nerdherd;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class statsActivity extends AppCompatActivity {

    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MenuController menuController;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_layout);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);

        setSupportActionBar(toolbar);

        menuController = new MenuController(statsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

//        recyclerView = findViewById(R.id.subscription_experiment_recyclerView);


        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        Log.d("Current exp title", targetexp.getTitle());
        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                for (Experiment allExperiment : experiments){
                    if (allExperiment.getTitle().contains(targetexp.getTitle())){
                        showList.add(allExperiment);
                    }
                }
                calculate(showList);
                //                showExperiments(recyclerView, showList);
            }

        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });






    }

    public void calculate(ArrayList<Experiment> showList){

        for (int counter = 0; counter < showList.size();counter++){
            BinomialTrial binomialTrial = (BinomialTrial)showList.get(counter).getTrials().get(0);
            Log.d("values", String.valueOf(showList.get(counter).getTrials().get(0)));
        }
    }
}
