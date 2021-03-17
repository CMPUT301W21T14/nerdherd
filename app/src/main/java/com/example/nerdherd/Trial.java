package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Trial extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private String trialType;
    private int Mintrials;
    private FloatingActionButton addtrials;

    ListView ExperimentList;
    ArrayAdapter<BinomialTrial> experimentAdapter;
    ArrayList<Experiment> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trials);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_trial_view);
        navigationView = findViewById(R.id.navigator);
        addtrials = findViewById(R.id.addTrial);

        setSupportActionBar(toolbar);

        menuController = new MenuController(Trial.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();

        Intent intent = getIntent();
        trialType = intent.getStringExtra("Trialtype");
        Mintrials = intent.getIntExtra("MinTrials",0);
        //check what tril we are working with (binomial, non-negative, count or..) and inflate fragment
        addtrials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trialType.equals("Binomial Trial")){
                    Intent intent2 = new Intent(Trial.this, BinomialTrial.class);
                    intent2.putExtra("minTrials", Mintrials);
                    startActivityForResult(intent2, 2);
                }
            }
        });
    }
    // get the Message from the other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            Log.d("success count", String.valueOf(data.getIntExtra("successCount",0)));
            int success = data.getIntExtra("successCount",0);
            int failure = data.getIntExtra("failureCount",0);
            BinomialTrial t2 = new BinomialTrial(success, failure);
            ArrayList<BinomialTrial> trials = new ArrayList<>();
            trials.add(t2);
//            dataList.add(new Experiment(trials));
//            experimentAdapter.notifyDataSetChanged();
        }
        if (requestCode ==1){

        }
    }

}
