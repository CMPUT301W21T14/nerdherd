package com.example.nerdherd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateExperimentActivity extends AppCompatActivity {

    private Spinner experimentTypeSpinner;
    private String[] experimentTypes = {"Binomial Trial", "Count", "Measurement", "Non-Negative Integer Count"};
    private String experimentType = "Binomial Trial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experiment);

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

    public void createExperiment(View view) {
        EditText editDescriptionView = (EditText) findViewById(R.id.experiment_description_editText);
        EditText minTrialsView = (EditText) findViewById(R.id.trial_number_editText);
        CheckBox requireLocationCheck = (CheckBox) findViewById(R.id.require_location_box);
        CheckBox publishExperimentCheck = (CheckBox) findViewById(R.id.publish_box);
        EditText editTitle = (EditText) findViewById(R.id.exp_title);

        String experimentDescription = editDescriptionView.getText().toString();
        String minTrialsString = minTrialsView.getText().toString();
        String experimentTitle = editTitle.getText().toString();

        int minTrials;

        if(experimentDescription.length() == 0) { //Blank Descriptions are invalid
            Toast.makeText(this, "Please enter a description for your experiment.", Toast.LENGTH_SHORT).show();
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

        //TODO Location Dialog will be implemented here later...

        Experiment createdExperiment = new Experiment(GlobalVariable.profile, experimentTitle,"Ongoing", experimentDescription, experimentType, minTrials, requireLocationCheck.isChecked(), publishExperimentCheck.isChecked());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("Action", "create"); //This tells the parent activity that it is receiving a created experiment allows the parent to tell the difference between different children when they return
        returnIntent.putExtra("newExperiment", createdExperiment);
        setResult(1, returnIntent);

        finish();
    }

    public void cancelCreation(View view) {
        finish();
    }
}