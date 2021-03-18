package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TrialActivity extends AppCompatActivity {

    private String trialType;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private FloatingActionButton addtrials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

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
        if (trialType.equals("Binomial Trial")){
//            Intent intent = new Intent(TrialActivity.this, BinomialTrialActivity.class);
//            startActivityForResult(intent, 2);
            addtrials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BinomialTrialDialogFragment().show(getSupportFragmentManager(), "EDIT_TEXT");
                }
            });
        }
        if (trialType.equals("Count")){
//            Intent intent = new Intent(TrialActivity.this, BinomialTrialActivity.class);
//            startActivityForResult(intent, 2);
            addtrials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CountTrialDialogFragment().show(getSupportFragmentManager(), "EDIT_TEXT2");
                }
            });
        }
        else{
            Log.d("trial type", trialType);
        }
    }

    public void updateBinomialTrialView(){

    }

    public void updateCountTrialView(){

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
