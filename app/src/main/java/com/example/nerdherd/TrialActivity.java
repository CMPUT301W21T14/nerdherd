package com.example.nerdherd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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
    private ArrayList<Integer> Testtrial4 = new ArrayList<>();
    ArrayList<BinomialTrial> binomialtrialing = new ArrayList<>();
    private ArrayList<Integer> binomtrialValues;
    ArrayList<CountTrial> counttrialing = new ArrayList<>();
    private ArrayList<Integer> counttrialValues;
    private ArrayList<Integer> trials_1;
    private ArrayList<Integer> counttrials_1;
    private GMailSender GMailSender;
    private ArrayList<Long> testing_1;
    private ArrayList<Long> testing_2 = new ArrayList<>();
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
        ArrayList<String> subscriberId = targetexp.getSubscriberId();
        //if false then user is not a subsctiber & cant participate
        // in a given experiment
        String flag = "false";
        for (int x =0; x < subscriberId.size(); x++){
            //if enters here one then valid & u are a subscriber
            if (GlobalVariable.profile.getId().equals(subscriberId.get(x))){
                    flag = "true";
            }
        }
        if (flag.equals("false")){
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
            testing_1 = new ArrayList<Long>();

            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Non-negative trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    adapter = new TrialsAdapter(list, listener, "Non-Negative Integer Count");
//                    Testtrial3 = (ArrayList<NonnegativeTrial>)list.clone();
                    for (int x = 0; x < list.size(); x++){
                        Testtrial3.add((NonnegativeTrial)list.get(x));
                    }
//                    Testtrial4 = (ArrayList<Integer>)list.clone();
                    for (int i = 0; i < Testtrial3.size(); ++i) {
                        for(int j = 0; j < Testtrial3.get(i).getNonNegativeTrials().size(); ++j) {
//                            testing_1.add((long) Math.toIntExact(Testtrial3.get(i).getNonNegativeTrials().get(j)));
                            testing_2.add(Long.valueOf(Testtrial3.get(i).getNonNegativeTrials().get(j)));
                        }
                    }
                    Log.d("test", String.valueOf(testing_2));
                    int sum_2 = 0;

                    Testtrial4 = (ArrayList<Integer>)testing_2.clone();
//                    for (int y =0; y < testing_2.size(); y++){
//                        sum_2+=testing_2.get(y);
//                    }
                    Long l2 = Long.valueOf(Testtrial4.get(4));
                    Log.d("tota sum", String.valueOf(l2));
//                    for (long y =0; y < testing_1.size(); y++){
//                        Math.toIntExact((testing_1.get().intValue()));
//                    }
                    //
//                    Log.d("testing", Test)
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
                    counttrialing = (ArrayList<CountTrial>) list.clone();
                    counttrialValues = Convertcount_val();
                    Log.d("test counted", counttrialValues.toString());
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

        Trial t1 = new BinomialTrial(success, failure);
        //experiment has trials
        //creating new experiment - but i want access to
        Log.d("test binomial", String.valueOf(binomialtrialing.size()));
        Log.d("Actual values", String.valueOf(binomtrialValues));
        int sum = 0;
        for (int y =0; y < binomtrialValues.size(); y++){
            sum+=binomtrialValues.get(y);
        }
        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        //total current trials - including the one just done by the user
        int fullSize = sum + (success + failure);
        Log.d("fullSize", String.valueOf(fullSize));

        //send email per instance of application - to remind the owner
        if (fullSize >= minTrial && GlobalVariable.success.equals("No")){
            Log.d("Exp is successfull", "Successful");
            String OwnerEmail = targetexp.getOwnerProfile().getEmail();
            String ExperimentName = targetexp.getTitle();
            String OwnerName = targetexp.getOwnerProfile().getName();
            //alert the user that the Experiment was a success
            sender("Successful Experiment", "Hello" + " "+OwnerName+ " "+"Your Experiment"
                    + " "+ "("+ExperimentName+")"+ " "+"is a success!", OwnerEmail);
            GlobalVariable.success = "yes";
        }
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

    public void updateCountTrialView(int[] count, int minTrial){

        Trial t1 = new CountTrial(count[0]);
        int counted = (count[0]);


        //experiment has trials
        //creating new experiment - but i want access to
        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        int sum = 0;
        for (int y =0; y < counttrialValues.size(); y++){
            sum+=counttrialValues.get(y);
        }
        int fullSize = sum + counted;
        Log.d("fullSize", String.valueOf(fullSize));
        //send email per instance of application - to remind the owner
        if (fullSize >= minTrial && GlobalVariable.success.equals("No")){
            Log.d("Exp is successfull", "Successful");
            String OwnerEmail = targetexp.getOwnerProfile().getEmail();
            String ExperimentName = targetexp.getTitle();
            String OwnerName = targetexp.getOwnerProfile().getName();
            //alert the user that the Experiment was a success
            sender("Successful Experiment", "Hello" + " "+OwnerName+ " "+"Your Experiment"
                    + " "+ "("+ExperimentName+")"+ " "+"is a success!", OwnerEmail);
            GlobalVariable.success = "yes";
        }
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
//            for (int i = 0; i < Testtrial3.size(); ++i) {
//                for(int j = 0; j < Testtrial3.get(i).getNonNegativeTrials().size(); ++j) {
//                    Log.d("this is type test", (Testtrial3.get(i).getNonNegativeTrials().get(j)).toString());
//                }
//            }
//            Log.d("this is type test", ((NonnegativeTrial)check.get(0)).getNonNegativeTrials().toString());
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
//        Testtrial2.add((MeasurementTrial) t1);
        //check after adding trials the size
        int fullSize = Testtrial2.size() + measurements.size();
        Log.d("fullSize", String.valueOf(fullSize));
        if (fullSize >= minTrial && GlobalVariable.success.equals("No")){
            Log.d("Exp is successfull", "Successful");
            String OwnerEmail = targetexp.getOwnerProfile().getEmail();
            String ExperimentName = targetexp.getTitle();
            String OwnerName = targetexp.getOwnerProfile().getName();
            //alert the user that the Experiment was a success
            sender("Successful Experiment", "Hello" + " "+OwnerName+ " "+"Your Experiment"
                    + " "+ "("+ExperimentName+")"+ " "+"is a success!", OwnerEmail);
            GlobalVariable.success = "yes";
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

    public ArrayList<Integer> Convertcount_val(){
        counttrials_1 = new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int y = 0; y < counttrialing.size(); y++){
            counttrials_1.add(counttrialing.get(y).getTotalCount());
        }
        return counttrials_1;
    }


    private void sender(String subject, String body, String target){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender = new GMailSender("richard15765@gmail.com", "ZhiPeng4!");
                    GMailSender.sendMail(subject, body, "richard15765@gmail.com", target);
                }catch(Exception e){
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }

}