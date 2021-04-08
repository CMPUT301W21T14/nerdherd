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

public class ExperimentViewActivity extends AppCompatActivity {

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
    private Button experimentEnd;
    private Button experimentPublish;
    private Button experimentSubscribe;
    private ExperimentE currentExperiment;

    private Button experimentQuestions;
    private Button experimentTrials;
    private Button experimentStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);

        ExperimentManager eMgr = ExperimentManager.getInstance();
        ProfileManager pMgr = ProfileManager.getInstance();
        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("experimentId");
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
            experimentRegion.setText(currentExperiment.getRegion().getDescription());
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

    }

    /*
            if (item.getItemId() == R.id.experiment_details && !(context instanceof ExperimentViewActivity)){
            intent = new Intent(context, ExperimentViewActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_trails && !(context instanceof TrialActivity)){
            intent = new Intent(context, TrialActivity.class);
            intent.putExtra("Type of Trial",trialType);
            intent.putExtra("Min of Trial", mintrial);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_stats && !(context instanceof statsactivity_checking)){
            intent = new Intent(context, statsactivity_checking.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_questions && !(context instanceof QuestionsActivity)) {
            intent = new Intent(context, QuestionsActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
     */
        /*index = GlobalVariable.indexForExperimentView;

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

        GlobalVariable.experimentType = experiment.getType();
        GlobalVariable.experimentMinTrials = experiment.getMinTrials();

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

                unpublishedSubscribe.setText("Unpublish");

                publishButtonHandler(publishIndicator, false);
            }
            else{
                unpublishedSubscribe.setText("Publish");

                publishButtonHandler(publishIndicator, true);
            }
            menuController.useMenu(true);
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
    }*/

    private void switcher(){
        Intent myExperimentIntent = new Intent(ExperimentViewActivity.this, MyExperimentsActivity.class);
        startActivity(myExperimentIntent);
        finish();
    }
}