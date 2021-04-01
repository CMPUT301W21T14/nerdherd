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

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Trial activity in the app
 * 4 types of trial extend this class to get themselves visible on the app
 * @author Ogooluwa S. osamuel
 */

public class TrialActivity extends AppCompatActivity {

    private String trialType;
    private String trialStatus;
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
    ArrayList<Trial> trialsList= new ArrayList<>();
    ArrayList<Trial> mTrialList = new ArrayList<>();
    ArrayList<Trial> TestList = new ArrayList<>();
    ArrayList<MeasurementTrial> Testtrial2 = new ArrayList<>();
    ArrayList<NonnegativeTrial> Testtrial3 = new ArrayList<>();
    //
    ArrayList<BinomialTrial> binomialtrialing = new ArrayList<>();
    private ArrayList<Integer> binomtrialValues;
    private ArrayList<Integer> trials_1;
    //
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
        if (targetexp.getStatus().equals("Ended")){
            addtrials.setVisibility(View.INVISIBLE);
        }
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

            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Binomial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    binomialtrialing = (ArrayList<BinomialTrial>) list.clone();
                    binomtrialValues = ConvertBinomial_val();

                    //Test to see if experiment is success - before allowing any trials
//                    testSucess(binomtrialValues, mintrials);
                    //
                    Log.d("testing binomial", String.valueOf(binomtrialValues.size()));
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
        if (trialType.equals("Non-Negative Integer Count")){
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Non-negative trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Non-Negative Integer Count");
                    Testtrial3 = (ArrayList<NonnegativeTrial>)list.clone();

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
                        Log.d("test", String.valueOf(TestList));
                        new NonnegativeTrialFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT2");
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
//                    Log.d("cval", String.valueOf(list));
                    Testtrial2 = (ArrayList<MeasurementTrial>) list.clone();
//                    Testtrial2.get(0).getMeasurements();
//                    Log.d("current val list", String.valueOf(Testtrial2.get(0).getMeasurements()));
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

    public void updateNonnegativeTrialView(ArrayList<Integer>negativeTrials, int minTrial){
        int val = negativeTrials.size();
        Log.d("hereee", "i got here");
        if (negativeTrials.size() < minTrial){
            Log.d("do not update", String.valueOf(negativeTrials.size()));

        }
        else{
//            Log.d("size of array", String.valueOf(NonnegativeTrials.size()));
            Log.d("passed values", String.valueOf(negativeTrials));
            Trial t2 = new NonnegativeTrial(negativeTrials);
            ((NonnegativeTrial) t2).setNonNegativeTrials(negativeTrials);
            //experiment has trials
            //creating new experiment - but i want access to
            Experiment targetexp2 = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
//            Log.d("testing", String.valueOf(TestList));
//            Testtrial3.add((NonnegativeTrial) t2);
            ArrayList <Trial> check = new ArrayList<>();
            check = (ArrayList<Trial>) Testtrial3.clone();
            targetexp2.setTrials(check);
//            Log.d("is dup", String.valueOf(Test))
            targetexp2.getTrials().add(t2);
            fireStoreController.addNewExperiment(targetexp2, new FireStoreController.FireStoreExperimentCallback() {
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
        }
    }
    public void updateMeasurementTrialView(ArrayList<Double> measurements, int minTrial){


        //Testtrial2 - contains all current trials
        //check if adding trials to Testtrial2 will result in success

        int size = measurements.size();
        Trial t1 = new MeasurementTrial(measurements);
//            ((MeasurementTrial) t1).setTotalMeasurementCount(size);
        ((MeasurementTrial) t1).setMeasurements(measurements);
        //experiment has trials
        //creating new experiment - but i want access to
        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        Log.d("testing", String.valueOf(TestList));
        Testtrial2.add((MeasurementTrial) t1);
        //check after adding trials the size
//        int fullSize = Testtrial2.size() +
        if (Testtrial2.size() >= minTrial){
            //alert the user that the Experiment was a success
            Log.d("Success", "Experiment is a sucess");
        }
        //
        ArrayList <Trial> check = new ArrayList<>();
        check = (ArrayList<Trial>) Testtrial2.clone();
        targetexp.setTrials(check);
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

    }
    //
    //convert to single array of binomial trials
    public ArrayList<Integer> ConvertBinomial_val(){
        trials_1 = new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int y = 0; y < binomialtrialing.size(); y++){
            trials_1.add(binomialtrialing.get(y).getSuccess());
            trials_1.add(binomialtrialing.get(y).getFailure());
        }
        return trials_1;
    }

//    public void testSucess(ArrayList<Integer> arr_val, int mintrials){
//        int size = arr_val.size();
//        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
//        String exp_title = (targetexp.getTitle());
//        String email = targetexp.getOwnerProfile().getEmail();
////        Log.d("test email", email);
//        if (size >= mintrials){
//            try {
//                GMailSender sender = new GMailSender("richard15765@gmail.com", "ZhiPeng4!");
//                sender.sendMail("",
//                        "Hello your experiment" + exp_title + "is a success",
//                        "richard15765@gmail.com",
//                        email);
//            } catch (Exception e) {
//                Log.e("SendMail", e.getMessage(), e);
//            }
//        }
//    }


}