package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * Shows all the experiments the logged in user is currently subscribed to
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */

public class MySubscriptionActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ExperimentAdapter.onClickListener listener;
    private MenuController menuController;
    private AdapterController adapterController;
    private ExperimentAdapter adapter;
    private TextView keywordView;
    private Button searchButton;
    private ArrayList<ExperimentE> experiments;
    private RecyclerView recyclerView;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscription);


        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_mySubscription);
        navigationView = findViewById(R.id.navigator);
        keywordView = findViewById(R.id.subscription_keyword_edit);
        searchButton = findViewById(R.id.subscription_search_button);


        setSupportActionBar(toolbar);

        menuController = new MenuController(MySubscriptionActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        recyclerView = findViewById(R.id.subscription_experiment_recyclerView);
        listener = new ExperimentAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if(experiments != null) {
                    ExperimentE e = experiments.get(index);
                    if(e == null) {
                        return;
                    }

                    Log.d("Clicked Exp: ", e.getDescription());

                    Intent experimentView = new Intent(MySubscriptionActivity.this, ExperimentViewActivity.class);
                    experimentView.putExtra("experimentId", e.getExperimentId());

                    startActivity(experimentView);
                    //finish(); //clicking back takes us to the previous activity
                }
            }
        };

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperiments();
            }
        });

        showExperiments();
        /*savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                showList.clear();

                for (Experiment allExperiment : experiments){
                    if (allExperiment.isPublished() && allExperiment.getSubscriberId().contains(GlobalVariable.profile.getId())){
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
                                new AlertDialog.Builder(MySubscriptionActivity.this).setTitle("No Result").setMessage("No result found. Please enter another keyword. Thank you.")
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
        });*/
    }

    // this sees if the user has pressed back button twice within 2 seconds to exit the app
    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void showExperiments() {
        experiments = new ArrayList<>();
        ExperimentManager eMgr = ExperimentManager.getInstance();
        for(String experimentId : LocalUser.getSubscribedExperiments()) {
            ExperimentE e = eMgr.getExperiment(experimentId);
            Log.d("Subscription: ", experimentId);
            if(e == null) {
                // Invalid - just ignore it I guess
                Log.d("Null", experimentId);
                continue;
            }
            if(!e.isPublished() && !e.getOwnerId().equals(LocalUser.getUserId())) {
                continue;
            }

            if(!eMgr.experimentContainsKeyword(e, keywordView.getText().toString()))  {
                continue;
            }
            experiments.add(e);
        }
        adapter = new ExperimentAdapter(experiments, listener);
        adapterController = new AdapterController(MySubscriptionActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

}