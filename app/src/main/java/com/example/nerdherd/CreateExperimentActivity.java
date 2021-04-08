package com.example.nerdherd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Experiment activity in the app
 * Experiments are shown on the app where the owner can create experiment for the users
 * @author Zhipeng Z. zhipeng4
 * @author Andrew D. adearbor
 * @author Tas S. saiyera
 */

public class CreateExperimentActivity extends AppCompatActivity implements ExperimentManager.ExperimentCreateEventListener {
    private Spinner experimentTypeSpinner;
    private String[] experimentTypes = {"Binomial Trial", "Count", "Measurement", "Non-Negative Integer Count"};
    private String experimentType = "Binomial Trial";
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> experimentList;
    private Boolean valid;
    private ArrayList<String> idList;

    private EditText etLongitude;
    private EditText etLattitude;
    private EditText etRange;
    private EditText etDescription;
    private LinearLayout llRangeContainer;

    private EditText editDescriptionView;
    private EditText minTrialsView;
    private CheckBox requireLocationCheck;
    private CheckBox publishExperimentCheck;
    private EditText editTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experiment);

        llRangeContainer = findViewById(R.id.region_container);
        etLattitude = findViewById(R.id.et_region_lattitude);
        etLongitude = findViewById(R.id.et_region_longitude);
        etRange = findViewById(R.id.et_region_range);
        etDescription = findViewById(R.id.et_region_descriptio);

        editDescriptionView = findViewById(R.id.experiment_description_editText);
        minTrialsView = findViewById(R.id.trial_number_editText);
        requireLocationCheck = findViewById(R.id.require_location_box);
        publishExperimentCheck = findViewById(R.id.publish_box);
        editTitle = findViewById(R.id.exp_title);

        llRangeContainer.setVisibility(View.GONE);

        requireLocationCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llRangeContainer.setVisibility(View.VISIBLE);
                } else {
                    llRangeContainer.setVisibility(View.GONE);
                }
            }
        });

        //Initialize the spinner drop down
        experimentTypeSpinner = (Spinner) findViewById(R.id.experiment_type_spinner);
        ArrayAdapter<String> experimentTypeList = new ArrayAdapter<String>(CreateExperimentActivity.this, android.R.layout.simple_spinner_item, experimentTypes);
        experimentTypeSpinner.setAdapter(experimentTypeList);
        experimentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                experimentType = experimentTypes[position]; //experiment type is tracked in real time unlike the other fields
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing if Nothing is Selected
            }
        });
    }

    public boolean validateInteger(String input) {
        try {
            int intInput = Integer.parseInt(input);
            if(intInput > 0) { //Need at least one trial
                return true;
            }
            return false;
        }
        catch(NumberFormatException e) { // False if its not a number
            return false;
        }
    }

    public double validateDouble(String input) {
        try {
            double val = Double.parseDouble(input);
            return val;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Owner is allowed to make changes to their owned trial for the users
     * @param view it on the app
     */

    public void createExperiment(View view) {


        String experimentDescription = editDescriptionView.getText().toString();
        String minTrialsString = minTrialsView.getText().toString();
        String experimentTitle = editTitle.getText().toString();


        double lattitude = validateDouble(etLattitude.getText().toString());
        double longitude = validateDouble(etLongitude.getText().toString());
        int range = (int) validateDouble(etRange.getText().toString());
        String regionDescription = etDescription.getText().toString();

        idList = new ArrayList<String>();

        int minTrials;

        if (experimentTitle.length() == 0 || experimentTitle.length() > 25){
            Toast.makeText(this, "The experiment title must be between 1 and 25 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(experimentDescription.length() == 0) { //Blank Descriptions are invalid
            Toast.makeText(this, "Please enter a description for your experiment.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(experimentDescription.length() > 50) { //description must be between 1 and 50 characters
            Toast.makeText(this, "The experiment description must be between 1 and 20 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if(minTrialsString.length() == 0) { //Blank minTrials is invalid
            Toast.makeText(this, "Please enter an Integer for the minimum number of trials.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(validateInteger(minTrialsString)) {
            minTrials = Integer.parseInt(minTrialsString);
        }
        else { //Any incorrect minTrial input will show this toast
            Toast.makeText(this, "The value entered for the minimum number of trials was invalid. Please enter an integer greater than 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Integer> typeMapHack = new HashMap<String, Integer>() {{
            put("Binomial Trial", ExperimentE.EXPERIMENT_TYPE_BINOMIAL);
            put("Count", ExperimentE.EXPERIMENT_TYPE_COUNT);
            put("Measurement", ExperimentE.EXPERIMENT_TYPE_MEASUREMENT);
            put("Non-Negative Integer Count", ExperimentE.EXPERIMENT_TYPE_NON_NEGATIVE);
        }};

        int experimentIntType = typeMapHack.get(experimentType);
        boolean requiresLocation = requireLocationCheck.isChecked();
        Region region = null;
        if(requiresLocation) {
            region = new Region(regionDescription, new GeoPoint(lattitude, longitude), range);
        }
        ExperimentManager eMgr = ExperimentManager.getInstance();
        eMgr.createExperiment(experimentTitle, experimentDescription, experimentIntType, minTrials, region, requiresLocation, publishExperimentCheck.isChecked(), this);

        //TODO Location Dialog will be implemented here later...
        /*
        fireStoreController = new FireStoreController();
        experimentList = new ArrayList<Experiment>();
        fireStoreController.createExperimentReader(experimentList, new FireStoreController.FireStoreCreateExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                valid = true;

                for (Experiment existedExperiment : experiments) {
                    if (existedExperiment.getTitle().equals(experimentTitle)) {
                        valid = false;
                    }
                }

                if (valid) {

                    if (requireLocationCheck.isChecked()){
                        String label = "Cinnamon & Toast";
                        String uriBegin = "geo:43.651070,79.347015";
                        String query = "43.651070,-79.347015(" + label + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery;
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent (Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }

                    Experiment createdExperiment = new Experiment(GlobalVariable.profile, experimentTitle, "Ongoing", experimentDescription, experimentType, minTrials, requireLocationCheck.isChecked(), publishExperimentCheck.isChecked(), idList, new ArrayList<Trial>());
                    fireStoreController.addNewExperiment(createdExperiment, new FireStoreController.FireStoreExperimentCallback() {
                        @Override
                        public void onCallback() {
                            finish();
                        }
                    }, new FireStoreController.FireStoreExperimentFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "The title is already used, please try another one. Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new FireStoreController.FireStoreCreateExperimentReadFailCallback(){
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });*/

        /*
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Action", "create"); //This tells the parent activity that it is receiving a created experiment allows the parent to tell the difference between different children when they return
        returnIntent.putExtra("newExperiment", createdExperiment);
        setResult(1, returnIntent);
         */
    }

    public void cancelCreation(View view) {
        finish();
    }

    @Override
    public void onCreateExperimentSuccess(ExperimentE createdExperiment) {
        finish();
    }

    @Override
    public void onCreateExperimentFailed(ExperimentE failedToCreate) {
        Toast.makeText(this, "Could not create experiment, try again later!", Toast.LENGTH_LONG);
    }
}