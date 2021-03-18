package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ExperimentViewActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private Intent previousIntent;
    private Integer index;
    private Experiment experiment;
    private TextView experimentTitle;
    private TextView experimentOwner;
    private TextView experimentStatus;
    private TextView experimentType;
    private TextView experimentRegion;
    private TextView experimentContact;
    private TextView experimentDescription;
    private Button experimentEnd;
    private Button unpublishedSubscribe;
    private FireStoreController fireStoreController;
    private Intent myExperimentIntent;
    private String experimentIndicator = "Experiment";
    private String publishIndicator = "Published";
    private ArrayList<String> idList;
    private Intent publicuser;
    private String val;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);

//        Bundle pUser = publicuser.getExtras();
//        if (pUser != null){
//            Intent intent = getIntent();
//            val = intent.getStringExtra("I Subscribed");
//
//        }
        experimentTitle = findViewById(R.id.experiment_title);
        experimentOwner = findViewById(R.id.experiment_owner);
        experimentStatus = findViewById(R.id.experiment_status);
        experimentType = findViewById(R.id.experiment_type);
        experimentRegion = findViewById(R.id.experiment_region);
        experimentContact = findViewById(R.id.experiment_contact);
        experimentDescription = findViewById(R.id.experiment_description);
        experimentEnd = findViewById(R.id.experiment_end);
        unpublishedSubscribe = findViewById(R.id.unpublish_subscribe_button);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_view);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ExperimentViewActivity.this, toolbar, navigationView, drawerLayout);

        index = GlobalVariable.indexForExperimentView;

        if (index != -1){
            experiment = GlobalVariable.experimentArrayList.get(index);
            experimentTitle.setText(experiment.getTitle());
            experimentOwner.setText(experiment.getOwnerProfile().getName());
            experimentStatus.setText(experiment.getStatus());
            experimentType.setText(experiment.getType());
            experimentRegion.setText("N/A");
            experimentContact.setText(experiment.getOwnerProfile().getEmail());
            experimentDescription.setText(experiment.getDescription());
            menuController.useMenu(false);
        }

        if (experiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
            if (experiment.getStatus().equals("Ended")) {
                experimentEnd.setText("Reopen");
                experimentEnd.setVisibility(View.VISIBLE);
                endButtonHandler("Ongoing");
            }
            else{
                experimentEnd.setText("End");
                experimentEnd.setVisibility(View.VISIBLE);
                endButtonHandler("Ended");
            }

            if (experiment.isPublished()) {

                unpublishedSubscribe.setText("Unpublished");

                publishButtonHandler(publishIndicator, false);
            }
            else{
                unpublishedSubscribe.setText("Publish");

                publishButtonHandler(publishIndicator, true);
            }

            menuController.useMenu(false);
        }
        else{
            idList = GlobalVariable.experimentArrayList.get(index).getSubscriberId();
            menuController.useMenu(false);

            if (idList.contains(GlobalVariable.profile.getId())){
                unpublishedSubscribe.setVisibility(View.INVISIBLE);
                menuController.useMenu(true);
                menuController.setTrialType(experiment.getType());
                menuController.setMinTrials(experiment.getMinTrials());
            }
            else {
                if (!experiment.getStatus().equals("Ended")) {
                    unpublishedSubscribe.setText("Subscribe");
                    unpublishedSubscribe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            idList.add(GlobalVariable.profile.getId());
                            GlobalVariable.experimentArrayList.get(index).setSubscriberId(idList);
                            fireStoreController = new FireStoreController();
                            fireStoreController.updater(experimentIndicator, experiment.getTitle(), "Subscriber Id", idList, new FireStoreController.FireStoreUpdateCallback() {
                                @Override
                                public void onCallback() {
                                    Toast.makeText(getApplicationContext(), "You are successfully subscribed. Thank you.", Toast.LENGTH_SHORT).show();
                                }
                            }, new FireStoreController.FireStoreUpdateFailCallback() {
                                @Override
                                public void onCallback() {
                                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            unpublishedSubscribe.setVisibility(View.INVISIBLE);
                            menuController.setTrialType(experiment.getType());
                            menuController.setMinTrials(experiment.getMinTrials());
                            menuController.useMenu(true);

                        }
                    });
                }
                else{
                    unpublishedSubscribe.setVisibility(View.INVISIBLE);
                    menuController.useMenu(false);
                }
            }
        }

    }


    private void publishButtonHandler(String indicator, Boolean isWhat){
        unpublishedSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariable.experimentArrayList.get(index).setPublished(isWhat);
                fireStoreController = new FireStoreController();
                fireStoreController.updater(experimentIndicator, experiment.getTitle(), indicator, isWhat, new FireStoreController.FireStoreUpdateCallback() {
                    @Override
                    public void onCallback() {
                        switcher();
                    }
                }, new FireStoreController.FireStoreUpdateFailCallback() {
                    @Override
                    public void onCallback() {
                        Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void endButtonHandler(String status){
        experimentEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariable.experimentArrayList.get(index).setStatus(status);
                fireStoreController = new FireStoreController();
                fireStoreController.updater(experimentIndicator, experiment.getTitle(), "Status", status, new FireStoreController.FireStoreUpdateCallback() {
                    @Override
                    public void onCallback() {
                        switcher();
                    }
                }, new FireStoreController.FireStoreUpdateFailCallback() {
                    @Override
                    public void onCallback() {
                        Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void switcher(){
        myExperimentIntent = new Intent(ExperimentViewActivity.this, MyExperimentsActivity.class);
        startActivity(myExperimentIntent);
        finish();
    }
}