package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.ObjectManager.ExperimentManager;

public class ExperimentResultsActivity extends AppCompatActivity {

    private ExperimentManager eMgr = ExperimentManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_results);

        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("experimentId");
        ExperimentE e = eMgr.getExperiment(experimentId);
        if(e == null) {
            Log.d("ExpResult", "exp=NULL");
            return;
        }
        // Show all the histograms and stuff here
    }
}