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

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.RecycleViewAdapters.ProfileListAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * The current logged in user sees his experiments for the app to store
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */

public class MyExperimentsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> returneditems;
    private Button searchButton;
    private TextView keywordView;
    private String keyword;
    private MyExperimentAdapter.onClickListener listener;
    private ArrayList<ExperimentE> experimentList;
    private SearchController searchController;
    private AdapterController adapterController;
    private MyExperimentAdapter adapter;

    private long backPressedTime;
    private Toast backToast;
    RecyclerView recyclerView;
    private ExperimentManager eMgr = ExperimentManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experiments);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.profileExp_layout);
        navigationView = findViewById(R.id.navigator);
        keywordView = findViewById(R.id.keywordEdit);
        searchButton = findViewById(R.id.searchBtn);


        setSupportActionBar(toolbar);

        menuController = new MenuController(MyExperimentsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        recyclerView = findViewById(R.id.experimentprofile_recyclerView);
        listener = new MyExperimentAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                ExperimentE e = experimentList.get(index);
                Intent intent = new Intent(MyExperimentsActivity.this, ExperimentViewActivity.class);
                intent.putExtra("experimentId", e.getExperimentId());
                startActivity(intent);
                //finish(); pressing back takes us to the previous activity, not the home screen
            }
        };

        showExperiments();

        /*

        savedList = new ArrayList<Experiment>();
        revealList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                revealList.clear();

                for (Experiment allExperiment : experiments){
                    if (allExperiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                        revealList.add(allExperiment);
                    }
                }

                searchController = new SearchController();
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyword = keywordView.getText().toString();
                        resultList = new ArrayList<>();
                        searchController.searchExperiment(keyword, revealList, resultList, new SearchController.ExperimentNoResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                new AlertDialog.Builder(MyExperimentsActivity.this).setTitle("No Result").setMessage("No result found. Please enter another keyword. Thank you.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                GlobalVariable.experimentArrayList = revealList;
                                                adapter = new MyExperimentAdapter(revealList, listener);
                                                adapterController = new AdapterController(MyExperimentsActivity.this, recyclerView, adapter);
                                                adapterController.useAdapter();
                                            }
                                        }).show();
                            }
                        }, new SearchController.ExperimentResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                GlobalVariable.experimentArrayList = resultList;
                                adapter = new MyExperimentAdapter(resultList, listener);
                                adapterController = new AdapterController(MyExperimentsActivity.this, recyclerView, adapter);
                                adapterController.useAdapter();
                            }
                        }, new SearchController.ExperimentNoKeywordCallBack() {
                            @Override
                            public void onCallback(ArrayList<Experiment> itemList) {
                                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                GlobalVariable.experimentArrayList = revealList;
                adapter = new MyExperimentAdapter(revealList, listener);
                adapterController = new AdapterController(MyExperimentsActivity.this, recyclerView, adapter);
                adapterController.useAdapter();
            }
        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void showExperiments() {
        experimentList = eMgr.getOwnedExperiments();
        adapter = new MyExperimentAdapter(experimentList, listener);
        adapterController = new AdapterController(MyExperimentsActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
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




//        getDescription();
        //i own experiements given that iam the owner
        //can use firestore to get all the experiments and return experiment where the users id
        //equals the userid in firestore


//    public void getDescription() {
//        savedList = new ArrayList<Experiment>();
//        fireStoreController = new FireStoreController();
//        returneditems = new ArrayList<Experiment>();
//
//        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
//            @Override
//            public void onCallback(ArrayList<Experiment> experiments) {
//
//
//                for (Experiment allExperiment:experiments) {
//                    if (allExperiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())) {
//                        Log.d("exp description",allExperiment.getDescription());
//                        savedList.add(allExperiment);
//                        returneditems.add(allExperiment);
//                    }
//                }
//            }
//        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
//            @Override
//            public void onCallback() {
//                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        Log.d("my experiment", returneditems.get(1).getDescription());
////        return returneditems.get(1).getDescription();
//    }


}