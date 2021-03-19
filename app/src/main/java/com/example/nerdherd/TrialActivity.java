package com.example.nerdherd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TrialActivity extends AppCompatActivity {

    private String trialType;
    private int mintrials;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private FloatingActionButton addtrials;
    private AdapterController adapterController;
    private TrialsAdapter adapter;
    private RecyclerView recyclerView;
    private FireStoreController fireStoreController = new FireStoreController();
    private ArrayList<Trial> trialArrayList = new ArrayList<Trial>();
    private TrialsAdapter.onClickListener listener;
    ArrayList<Experiment> dataList;
    ListView ExperimentList;
    ArrayAdapter<Experiment> experimentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

        dataList = new ArrayList<Experiment>();
        recyclerView = findViewById(R.id.list_recyclerView);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_trial_view);
        navigationView = findViewById(R.id.navigator);
        addtrials = findViewById(R.id.addTrial);
        setSupportActionBar(toolbar);

        menuController = new MenuController(TrialActivity.this, toolbar, navigationView, drawerLayout);

        menuController.useMenu(true);

        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);

        //types of trials:
        //"Binomial Trial", "Count", "Measurement", "Non-Negative Integer Count"

        trialType = GlobalVariable.experimentType;
        mintrials = GlobalVariable.experimentMinTrials;
        ///ArrayList<Integer> current_exp

        listener = new TrialsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if (targetexp.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())) {
                    new AlertDialog.Builder(TrialActivity.this).setTitle("Ignore").setMessage("Do you want to ignore this result?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    targetexp.getTrials().remove(index);
                                    fireStoreController.updater("Experiment", targetexp.getTitle(), "Trial List", targetexp.getTrials(), new FireStoreController.FireStoreUpdateCallback() {
                                        @Override
                                        public void onCallback() {
                                            return;
                                        }
                                    }, new FireStoreController.FireStoreUpdateFailCallback() {
                                        @Override
                                        public void onCallback() {
                                            return;
                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    }).show();
                }
            }
        };


        if (trialType.equals("Binomial Trial")){
//            Intent intent = new Intent(TrialActivity.this, BinomialTrialActivity.class);
//            startActivityForResult(intent, 2);
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Binomial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Binomial Trial");
                    adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
                    adapterController.useAdapter();
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
            if (targetexp.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                addtrials.setVisibility(View.INVISIBLE);
            }
            else {
                addtrials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new BinomialTrialDialogFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT");
                    }
                });
            }
        }
        if (trialType.equals("Count")){
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Count trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Count");
                    adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
                    adapterController.useAdapter();
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
            if (targetexp.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                addtrials.setVisibility(View.INVISIBLE);
            }
            else {
                addtrials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CountTrialDialogFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT2");
                    }
                });
            }
        }
        if (trialType.equals("Measurement")){
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Measurement trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Measurement");
                    adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
                    adapterController.useAdapter();
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
            if (targetexp.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                addtrials.setVisibility(View.INVISIBLE);
            }
            else {
                addtrials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MeaurementTrialFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT3");
                    }
                });
            }
        }

        if (trialType.equals("Non-Negative Integer Count")){

            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Non-negative trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Non-Negative Integer Count");
                    adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
                    adapterController.useAdapter();
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });

            if (targetexp.getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                addtrials.setVisibility(View.INVISIBLE);
            }
            else {
                addtrials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NonnegativeTrialFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT4");
                    }
                });
            }
        }
    }


    private void showTrials(RecyclerView recyclerView, ArrayList<Trial> trials){
        //use adapter
    }

    public void updateBinomialTrialView(int success, int failure, int minTrial){
        if (success + failure < minTrial){
            Log.d("do not update", String.valueOf(success));
        }
        else{
            Trial t1 = new BinomialTrial(success, failure);
            //experiment has trials
            //creating new experiment - but i want access to
            Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
            targetexp.getTrials().add(t1);
            fireStoreController.addNewExperiment(targetexp, new FireStoreController.FireStoreExperimentCallback() {
                @Override
                public void onCallback() {
                    return;
                }
            }, new FireStoreController.FireStoreExperimentFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });

            //targetexp.getTrials();
        }
    }

    public void updateCountTrialView(int[] count, int minTrial){
        if (count[0] < minTrial){
            Log.d("do not update", String.valueOf(count[0]));
        }
        else{
            Trial t1 = new CountTrial(count[0]);
            Log.d("counteddd", String.valueOf(count[0]));
            //experiment has trials
            //creating new experiment - but i want access to
            Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
            targetexp.getTrials().add(t1);

            fireStoreController.addNewExperiment(targetexp, new FireStoreController.FireStoreExperimentCallback() {
                @Override
                public void onCallback() {
                    return;
                }
            }, new FireStoreController.FireStoreExperimentFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
//
        }
    }
    public void updateMeasurementTrialView(ArrayList<Double> measurements, int minTrial){
        if (measurements.size() < minTrial){
            Log.d("do not update", String.valueOf(measurements.size()));

        }
        else{
            Trial t1 = new MeasurementTrial(measurements.size());
            //experiment has trials
            //creating new experiment - but i want access to
            Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
            targetexp.getTrials().add(t1);


            fireStoreController.addNewExperiment(targetexp, new FireStoreController.FireStoreExperimentCallback() {
                @Override
                public void onCallback() {
                    return;
                }
            }, new FireStoreController.FireStoreExperimentFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });

//            //targetexp.getTrials();
//            adapter = new TrialsAdapter(targetexp.getTrials(), null, "Measurement trial");
//            adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
//            adapterController.useAdapter();
//
        }
    }

    public void updateNonnegativeTrialView(ArrayList<Integer>NonnegativeTrials, int minTrial){
        if (NonnegativeTrials.size() < minTrial){
            Log.d("do not update", String.valueOf(NonnegativeTrials.size()));

        }
        else{
            Trial t1 = new NonnegativeTrial(NonnegativeTrials.size());
            //experiment has trials
            //creating new experiment - but i want access to
            Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
            targetexp.getTrials().add(t1);

            fireStoreController.addNewExperiment(targetexp, new FireStoreController.FireStoreExperimentCallback() {
                @Override
                public void onCallback() {
                    return;
                }
            }, new FireStoreController.FireStoreExperimentFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
//            //targetexp.getTrials();
//            adapter = new TrialsAdapter(targetexp.getTrials(), null, "Nonnegative_Integer trial");
//            adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
//            adapterController.useAdapter();
//
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        // validating same result code as what was passed above
//        if(requestCode==2)
//        {
//
//        }
//    }

}
