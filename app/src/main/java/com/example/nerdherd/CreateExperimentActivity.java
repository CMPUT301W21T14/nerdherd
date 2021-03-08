package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateExperimentActivity extends AppCompatActivity {

    private Spinner experimentTypeSpinner;
    private String[] experimentTypes = {"Binomial Trial", "Count", "Measurement", "Non-Negative Integer Count"};
    private String experimentType = "Binomial Trial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experiment);


        experimentTypeSpinner = (Spinner) findViewById(R.id.experiment_type_spinner);
        ArrayAdapter<String> experimentTypeList = new ArrayAdapter<String>(CreateExperimentActivity.this, android.R.layout.simple_spinner_item, experimentTypes);
        experimentTypeSpinner.setAdapter(experimentTypeList);
        experimentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                experimentType = experimentTypes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing if Nothing is Selected
            }
        });
    }

    public void createExperiment(View view) {

    }

    public void cancelCreation(View view) {

    }
}