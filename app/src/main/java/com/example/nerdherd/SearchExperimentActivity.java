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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    private SearchController searchController;
    private TextView keywordView;
    private Button searchButton;
    private String keyword;
    private ArrayList<Experiment> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experiment);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);
        keywordView = findViewById(R.id.keyword_edit);
        searchButton = findViewById(R.id.search_button);


        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchExperimentActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        RecyclerView recyclerView = findViewById(R.id.experiment_recyclerView);
        listener = new ExperimentAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                experimentView = new Intent(SearchExperimentActivity.this, ExperimentViewActivity.class);
                GlobalVariable.indexForExperimentView = index;
                startActivity(experimentView);
                finish();
            }
        };

        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                showList.clear();

                for (Experiment allExperiment : experiments){
                    if (allExperiment.isPublished()){
                        showList.add(allExperiment);
                    }
                }

                searchController = new SearchController();
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyword = keywordView.getText().toString();
                        resultList = new ArrayList<>();
                        searchController.searchExperiment(keyword, showList, resultList, new SearchController.ExperimentNoResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                new AlertDialog.Builder(SearchExperimentActivity.this).setTitle("No Result").setMessage("No result found. Please enter another keyword. Thank you.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                showExperiments(recyclerView, itemList);
                                            }
                                        }).show();
                            }
                        }, new SearchController.ExperimentResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                showExperiments(recyclerView, itemList);
                            }
                        }, new SearchController.ExperimentNoKeywordCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                Toast.makeText(getApplicationContext(), "Please enter a keyword. Thank you.", Toast.LENGTH_SHORT).show();
                                showExperiments(recyclerView, itemList);
                            }
                        });
                    }
                });

                showExperiments(recyclerView, showList);
            }
        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Start the CreateExperiment Activity on button press
    public void createExperimentButton(View view) {
        Intent createExpIntent = new Intent(SearchExperimentActivity.this, CreateExperimentActivity.class);
        startActivity(createExpIntent);
    }

    private void showExperiments(RecyclerView recyclerView, ArrayList<Experiment> experiments){
        GlobalVariable.experimentArrayList = experiments;
        adapter = new ExperimentAdapter(experiments, listener);
        adapterController = new AdapterController(SearchExperimentActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

}