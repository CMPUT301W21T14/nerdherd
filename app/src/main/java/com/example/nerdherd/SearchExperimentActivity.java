package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class SearchExperimentActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ExperimentAdapter.onClickListener listener;
    private ArrayList<Experiment> experimentList;
    private MenuController menuController;
    private AdapterController adapterController;
    private ExperimentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experiment);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchExperimentActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();

        RecyclerView recyclerView = findViewById(R.id.experiment_recyclerView);
        listener = new ExperimentAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                Log.d("HaHaHa","Still in test");
            }
        };
// Test below
        experimentList = new ArrayList<Experiment>();
        experimentList.add(new Experiment("owner", "status", "title"));
// Test above
        adapter = new ExperimentAdapter(experimentList, listener);
        adapterController = new AdapterController(SearchExperimentActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

/*
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        super.onActivityResult(reqCode, resCode, intent);

        // Get the created Experiment from CreateExperimentActivity
        if(intent.getStringExtra("Action").compareTo("create") == 0) { //Use this to differ between different returning Activities
            Experiment newExperiment = (Experiment) intent.getSerializableExtra("newExperiment");
            experimentList.add(newExperiment); // add experiment to experimentList
            FireStoreController firestoreController = new FireStoreController();
            firestoreController.addNewExperiment(newExperiment); // add experiment to database
        }
    }
 */

    // Start the CreateExperiment Activity on button press
    public void createExperimentButton(View view) {
        Intent createExpIntent = new Intent(SearchExperimentActivity.this, CreateExperimentActivity.class);
        startActivity(createExpIntent);
    }


}