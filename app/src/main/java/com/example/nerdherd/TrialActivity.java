package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

        //types of trials:
        //"Binomial Trial", "Count", "Measurement", "Non-Negative Integer Count"

        trialType = getIntent().getStringExtra("Type of Trial");
        mintrials = getIntent().getIntExtra("Min of Trial", -1);
        if (trialType.equals("Binomial Trial")){
//            Intent intent = new Intent(TrialActivity.this, BinomialTrialActivity.class);
//            startActivityForResult(intent, 2);
            addtrials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BinomialTrialDialogFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT");
                }
            });
        }
        if (trialType.equals("Count")){
            addtrials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CountTrialDialogFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT2");
                }
            });
        }
        if (trialType.equals("Measurement")){

            addtrials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MeaurementTrialFragment(mintrials).show(getSupportFragmentManager(), "EDIT_TEXT3");
                }
            });
        }
        else{
            Log.d("trial type", trialType);
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

            Log.d("something", "trying new"+ targetexp.getTrials().toString());

            //targetexp.getTrials();
            adapter = new TrialsAdapter(targetexp.getTrials(), null);
            adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
            adapterController.useAdapter();
            adapter.notifyDataSetChanged();
//
        }
    }

    public void updateCountTrialView(int[] count, int minTrial){
        if (count[0] < minTrial){
            Log.d("do not update", String.valueOf(count[0]));
        }
    }
    public void updateMeasurementTrialView(ArrayList<Double> measurements, int minTrial){
        if (measurements.size() < minTrial){
            Log.d("do not update", String.valueOf(measurements.size()));

        }
        else{
            Log.d("array val confirm", String.valueOf(measurements.get(minTrial-1)));
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
