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
    private MenuController menuController;
    private AdapterController adapterController;
    private ExperimentAdapter adapter;
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private Intent experimentView;

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
                experimentView = new Intent(SearchExperimentActivity.this, ExperimentViewActivity.class);
                experimentView.putExtra("index", index);
                startActivity(experimentView);
                finish();
            }
        };

        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();

        /*
        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentListCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                showList.clear();

                for (Experiment allExperiment : experiments){
                    if (allExperiment.isPublished()){
                        showList.add(allExperiment);
                    }
                }

                adapter = new ExperimentAdapter(showList, listener);
                adapterController = new AdapterController(SearchExperimentActivity.this, recyclerView, adapter);
                adapterController.useAdapter();
            }
        }, new FireStoreController.FireStoreExperimentListFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
        
         */
    }

    // Start the CreateExperiment Activity on button press
    public void createExperimentButton(View view) {
        Intent createExpIntent = new Intent(SearchExperimentActivity.this, CreateExperimentActivity.class);
        startActivity(createExpIntent);
    }


}