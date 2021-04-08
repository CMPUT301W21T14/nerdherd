package com.example.nerdherd;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * Trial activity in the app
 * 4 types of trial extend this class to get themselves visible on the app
 * @author Ogooluwa S. osamuel
 */

public class TrialActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    public static final int PERMISSIONS_REQUEST_LOCATION = 99;

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
    private ArrayList<Long> Arraylongs = new ArrayList<>();
    private ArrayList<Integer> Testtrial4 = new ArrayList<>();
    ArrayList<BinomialTrial> binomialtrialing = new ArrayList<>();
    private ArrayList<Integer> binomtrialValues;
    ArrayList<CountTrial> counttrialing = new ArrayList<>();
    private ArrayList<Integer> counttrialValues;
    private ArrayList<Integer> trials_1;
    private ArrayList<Integer> counttrials_1;
    private GMailSender GMailSender;
    private ArrayList<Long> testing_1;
    private ArrayList<Integer> testing_2 = new ArrayList<>();
    ArrayList<NonnegativeTrial> nonNegativetrialing = new ArrayList<>();
    private ArrayList<Integer> nonNegativetrialValues;
    private ArrayList<Integer> test_nonNegative;
    //
    ListView ExperimentList;
    ArrayAdapter<Experiment> experimentAdapter;

    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private ProfileManager pMgr = ProfileManager.getInstance();
    private String experimentId;
    private ExperimentE experiment;
    private ArrayList<TrialT> trialList;

    private Button ignoreUserBtn;
    private EditText ignoreUserEt;
    private TextView locRequiredTv;
    private TextView ignoreUserTv;

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

        ignoreUserBtn = findViewById(R.id.btn_ignore_experimenter);
        ignoreUserEt = findViewById(R.id.et_ignore_experimenter);
        locRequiredTv = findViewById(R.id.tv_location_required);
        ignoreUserTv = findViewById(R.id.tv_blacklist_label);


        eMgr.addOnChangeListener(this);

        menuController = new MenuController(TrialActivity.this, toolbar, navigationView, drawerLayout);

        menuController.useMenu(true);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("experimentId");
        experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            Log.d("TrialActivity", "exp=NULL");
            return;
        }

        if(!experiment.getOwnerId().equals(LocalUser.getUserId())) {
            ignoreUserBtn.setVisibility(View.GONE);
            ignoreUserEt.setVisibility(View.GONE);

        }

        if(experiment.isLocationRequired()) {
            beginLocationUpdates();
        } else {
            locRequiredTv.setVisibility(View.GONE);
        }

        FloatingActionButton addTrialButton = findViewById(R.id.addTrial);
        addTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(experiment.getType()) {
                    case ExperimentE.EXPERIMENT_TYPE_BINOMIAL:
                        new BinomialTrialDialogFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT");
                        break;
                    case ExperimentE.EXPERIMENT_TYPE_COUNT:
                        new CountTrialDialogFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT2");
                        break;
                    case ExperimentE.EXPERIMENT_TYPE_NON_NEGATIVE:
                        new NonnegativeTrialFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT2");
                        break;
                    case ExperimentE.EXPERIMENT_TYPE_MEASUREMENT:
                        new MeaurementTrialFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT3");
                        break;
                }
            }
        });

        listener = new TrialsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if(trialList != null) {
                    TrialT t = trialList.get(index);
                    if(t == null) {
                        return;
                    }
                    UserProfile up = pMgr.getProfile(t.getExperimenterId());
                    if(up == null) {
                        Log.d("TrialAct", "up=NULL");
                    }
                    ignoreUserEt.setText(up.getUserName());
                }
            }
        };

        ignoreUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eMgr.addUserToExperimentBlacklist(ignoreUserEt.getText().toString(), experimentId)) {
                    ignoreUserEt.setText("User Ignored!");
                } else {
                    invalidBlacklistUser();
                }
            }
        });

        showTrials();
    }

    public void invalidBlacklistUser() {
        Toast.makeText(this, "Invalid Username", Toast.LENGTH_LONG).show();
    }

    public void addMeasurementTrial(float outcome) {
        eMgr.addTrialToExperiment(experimentId, new TrialT(LocalUser.getUserId(), outcome, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addCountTrial() {
        eMgr.addTrialToExperiment(experimentId, new TrialT(LocalUser.getUserId(), 1, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addNonNegativeTrial(int outcome) {
        eMgr.addTrialToExperiment(experimentId, new TrialT(LocalUser.getUserId(), outcome, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addSuccessfulBinomialTrial() {
        eMgr.addTrialToExperiment(experimentId, new TrialT(LocalUser.getUserId(), 1, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addUnsuccessfulBinomialTrial() {
        eMgr.addTrialToExperiment(experimentId, new TrialT(LocalUser.getUserId(), 0, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    private void beginLocationUpdates() {
        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(TrialActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Can use lastLocation for the location of trials
                    // Remember to convert to a GeoPoint for firebase
                    LocalUser.setLastLocation(location);
                    Log.d("LastLoc: ", location.toString());
                }
            });
        }
    }

    private void showTrials(){
        trialList = eMgr.getTrialsExcludeBlacklist(experimentId);
        RecyclerView recyclerView = findViewById(R.id.list_recyclerView);
        adapter = new TrialsAdapter(trialList, listener);
        adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        showTrials();
    }

        /*

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
                    nonNegativetrialing = (ArrayList<NonnegativeTrial>) list.clone();
                    nonNegativetrialValues = nonNegative_val();

                    int sum_2 = 0;

//                    Testtrial4 = (ArrayList<Integer>)testing_2.clone();
//                    for (int y =0; y < testing_2.size(); y++){
//                        sum_2+=testing_2.get(y);
//                    }
//                    Long l2 = Long.valueOf(Testtrial4.get(4));
//                    Log.d("tota sum", String.valueOf(l2));
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

        Trial t1 = new BinomialTrial(success, failure, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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

        Trial t1 = new CountTrial(count[0],new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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

    public void updateNonnegativeTrialView(ArrayList<Long>negativeTrials, int minTrial){

        Log.d("database", nonNegativetrialValues.toString());
        Log.d("hereee", "i got here");
        int val = negativeTrials.size();

        //sum of current trials
        int counted = 0;
        for (int i=0;i<val;++i) {
            counted+=negativeTrials.get(i).longValue();
        }

        Log.d("arrays", negativeTrials.toString());
//            Log.d("size of array", String.valueOf(NonnegativeTrials.size()));
        Log.d("passed values", String.valueOf(negativeTrials));
        Trial t2 = new NonnegativeTrial(negativeTrials, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        ((NonnegativeTrial) t2).setNonNegativeTrials(negativeTrials);

        //experiment has trials
        //creating new experiment - but i want access to
        Experiment targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        //take sum of previous trials
        int sum = 0;
        for (int y =0; y < nonNegativetrialValues.size(); y++){
            sum+=nonNegativetrialValues.get(y);
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
        ArrayList <Trial> check = new ArrayList<>();
        check = (ArrayList<Trial>) nonNegativetrialing.clone();
        Log.d("testing set", check.toString());
        targetexp.setTrials(check);
        targetexp.getTrials().add(t2);
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
    public void updateMeasurementTrialView(ArrayList<Double> measurements, int minTrial){


        //Testtrial2 - contains all current trials
        //check if adding trials to Testtrial2 will result in success

        int size = measurements.size();
        Trial t1 = new MeasurementTrial(measurements, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Integer> nonNegative_val(){
        test_nonNegative= new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < nonNegativetrialing.size(); ++i) {
            for(int j = 0; j < nonNegativetrialing.get(i).getNonNegativeTrials().size(); ++j) {
                test_nonNegative.add(Math.toIntExact(nonNegativetrialing.get(i).getNonNegativeTrials().get(j)));
            }
        }
        return test_nonNegative;
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
    }*/

}