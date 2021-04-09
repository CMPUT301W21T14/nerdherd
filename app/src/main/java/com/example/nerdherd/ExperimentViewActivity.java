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

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * Helps in viewing the experiment details on the app
 * @author Zhipeng Z. zhipeng4
 * @author Andrew D. adearbor
 * @author Tas S. saiyera
 */

public class ExperimentViewActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private TextView experimentTitle;
    private TextView experimentOwner;
    private TextView experimentStatus;
    private TextView experimentType;
    private TextView experimentRegion;
    private TextView experimentContact;
    private TextView experimentDescription;
    private TextView minTrialsTv;
    private Button experimentEnd;
    private Button experimentPublish;
    private Button experimentSubscribe;
    private ExperimentE currentExperiment;

    private Button experimentQuestions;
    private Button experimentTrials;
    private Button experimentStatistics;
    private Button experimentGeoMap;

    private String experimentId;

    private ExperimentManager eMgr = ExperimentManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);

        ProfileManager pMgr = ProfileManager.getInstance();
        Intent intent = getIntent();
        experimentId = intent.getStringExtra("experimentId");
        currentExperiment = eMgr.getExperiment(experimentId);
        if (currentExperiment == null) {
            // big uh oh
            Log.d("ExperimentView", "NULL EXPERIMENT");
            finish();
            return;
        }

        experimentQuestions = findViewById(R.id.btn_view_questions);
        experimentTrials = findViewById(R.id.btn_add_trials);
        experimentStatistics = findViewById(R.id.btn_view_stats);
        experimentGeoMap = findViewById(R.id.btn_view_geomap);

        experimentTitle = findViewById(R.id.experiment_title);
        experimentOwner = findViewById(R.id.experiment_owner);
        experimentStatus = findViewById(R.id.experiment_status);
        experimentType = findViewById(R.id.experiment_type);
        experimentRegion = findViewById(R.id.experiment_region);
        experimentContact = findViewById(R.id.experiment_contact);
        experimentDescription = findViewById(R.id.experiment_description);
        experimentEnd = findViewById(R.id.btn_end_experimentresults);
        experimentPublish = findViewById(R.id.btn_publish_experiment);
        experimentSubscribe = findViewById(R.id.btn_subscribe_experiment);
        minTrialsTv = findViewById(R.id.tv_min_trials);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_view);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ExperimentViewActivity.this, toolbar, navigationView, drawerLayout);
        //menuController.useMenu(true);

        UserProfile ownerProfile = pMgr.getProfile(currentExperiment.getOwnerId());
        if (ownerProfile != null) {
            experimentTitle.setText(currentExperiment.getTitle());
            experimentDescription.setText(currentExperiment.getDescription());
            experimentOwner.setText(ownerProfile.getUserName());
            experimentStatus.setText(currentExperiment.getStatus());
            experimentType.setText(currentExperiment.typeToString());
            Region region = currentExperiment.getRegion();
            if(region == null) {
                experimentRegion.setText("N/A");
            } else {
                experimentRegion.setText(region.getDescription());
            }

            experimentContact.setText(ownerProfile.getContactInfo());
        }

        if (!LocalUser.getUserId().equals(ownerProfile.getUserId())) {
            experimentPublish.setVisibility(View.GONE);
            if(currentExperiment.getStatus().equals("Ended")) {
                experimentEnd.setText("View Results");
            } else {
                experimentEnd.setVisibility(View.GONE);
            }

        }

        if(LocalUser.isSubscribed(experimentId)) {
            experimentSubscribe.setText("Unsubscribe");
        } else {
            experimentSubscribe.setText("Subscribe");
        }

        if(currentExperiment.isPublished()) {
            experimentPublish.setText("Unpublish");
        } else {
            experimentPublish.setText("Unpublish");
        }

        if(currentExperiment.getStatus().equals("Ended")) {
            experimentPublish.setVisibility(View.GONE);
            experimentTrials.setVisibility(View.GONE);

        }

        experimentSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LocalUser.isSubscribed(experimentId)) {
                    eMgr.unsubscribeToExperiment(experimentId);
                    experimentSubscribe.setText("Subscribe");
                } else {
                    eMgr.subscribeToExperiment(experimentId);
                    experimentSubscribe.setText("Unsubscribe");
                }
            }
        });

        experimentEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentExperiment.getStatus().equals("Ended")) {
                    //
                    Intent nintent = new Intent(ExperimentViewActivity.this, ExperimentResultsActivity.class);
                    nintent.putExtra("experimentId", experimentId);
                    startActivity(nintent);
                } else {
                    eMgr.endExperiment(experimentId);
                    experimentStatus.setText("Ended");
                    experimentEnd.setText("View Results");
                }
            }
        });

        experimentPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentExperiment.isPublished()) {
                    eMgr.unpublishExperiment(experimentId);
                    experimentPublish.setText("Publish");
                } else {
                    eMgr.publishExperiment(experimentId);
                    experimentPublish.setText("Unpublish");
                }
            }
        });

        experimentQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(ExperimentViewActivity.this, QuestionsActivity.class);
                nintent.putExtra("experimentId", experimentId);
                startActivity(nintent);
            }
        });

        experimentStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(ExperimentViewActivity.this, statsactivity_checking.class);
                nintent.putExtra("experimentId", experimentId);
                startActivity(nintent);
            }
        });

        experimentTrials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(ExperimentViewActivity.this, TrialActivity.class);
                nintent.putExtra("experimentId", experimentId);
                startActivity(nintent);
            }
        });

        experimentGeoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Maps activity entry point
                Intent nintent = new Intent(ExperimentViewActivity.this, TrialHeatmapActivity.class);
                nintent.putExtra("experimentId", experimentId);
                startActivity(nintent);
            }
        });

        updateTrialGoal();
    }

    private void switcher(){
        Intent myExperimentIntent = new Intent(ExperimentViewActivity.this, MyExperimentsActivity.class);
        startActivity(myExperimentIntent);
        finish();
    }

    private void updateTrialGoal() {
        int target = currentExperiment.getMinimumTrials();
        int current = eMgr.getTrialCount(experimentId);
        String value = "Trial Count: "+current+"/"+target;
        minTrialsTv.setText(value);
        if(current > target) {
            minTrialsTv.setTextColor(getResources().getColor(R.color.ic_launcher_background));
        } else {
            minTrialsTv.setTextColor(getResources().getColor(R.color.Gradient));
        }
    }

    @Override
    public void onExperimentDataChanged() {
        updateTrialGoal();
    }
}