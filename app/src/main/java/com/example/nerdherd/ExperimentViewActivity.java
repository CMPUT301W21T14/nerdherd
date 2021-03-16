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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);

        experimentTitle = findViewById(R.id.experiment_title);
        experimentOwner = findViewById(R.id.experiment_owner);
        experimentStatus = findViewById(R.id.experiment_status);
        experimentType = findViewById(R.id.experiment_type);
        experimentRegion = findViewById(R.id.experiment_region);
        experimentContact = findViewById(R.id.experiment_contact);
        experimentDescription = findViewById(R.id.experiment_description);
        experimentEnd = findViewById(R.id.experiment_end);
        unpublishedSubscribe = findViewById(R.id.unpublish_subscribe_button);

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
        }

        if (experiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
            if (experiment.getStatus().equals("Ended")) {
                unpublishedSubscribe.setVisibility(View.INVISIBLE);
            }
            else{
                experimentEnd.setVisibility(View.VISIBLE);
                unpublishedSubscribe.setText("Unpublished");
            }

            experimentEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariable.experimentArrayList.get(index).setStatus("Ended");
                    fireStoreController = new FireStoreController();
                    fireStoreController.updater(experimentIndicator, experiment.getTitle(), "Status", "Ended", new FireStoreController.FireStoreUpdateCallback() {
                        @Override
                        public void onCallback() {
                            myExperimentIntent = new Intent(ExperimentViewActivity.this, MyExperimentsActivity.class);
                            startActivity(myExperimentIntent);
                            finish();
                        }
                    }, new FireStoreController.FireStoreUpdateFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            unpublishedSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariable.experimentArrayList.get(index).setPublished(false);
                    fireStoreController = new FireStoreController();
                    fireStoreController.updater(experimentIndicator, experiment.getTitle(), "Published", false, new FireStoreController.FireStoreUpdateCallback() {
                        @Override
                        public void onCallback() {
                            myExperimentIntent = new Intent(ExperimentViewActivity.this, MyExperimentsActivity.class);
                            startActivity(myExperimentIntent);
                            finish();
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

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_view);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ExperimentViewActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();
    }
}