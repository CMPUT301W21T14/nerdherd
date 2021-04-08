package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.FirestoreAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;

/**
 * Splash screen for the app
 * The constant screen that is played for a fixed amount of time when the app is opened
 * @author Zhipeng Z. zhipeng4
 */

public class SplashScreen extends AppCompatActivity implements ProfileManager.ProfileDataLoadedEventListener {

    private Intent logInIntent;
    private Integer time = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeData(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logInIntent = new Intent(SplashScreen.this, LogInActivity.class);
                startActivity(logInIntent);
                finish();
            }
        }, time);
    }

    private void InitializeData(boolean useMockData) {
        // Create a database adapter to use with our managers
        DatabaseAdapter dbAdapter;
        if(useMockData) {
            LocalUser.setMockDBUsed();
            dbAdapter = new MockDatabaseAdapater();
        } else {
            dbAdapter = new FirestoreAdapter();
        }

        ExperimentManager eMgr = ExperimentManager.getInstance();
        ProfileManager pMgr = ProfileManager.getInstance();

        eMgr.setDatabaseAdapter(dbAdapter);
        pMgr.setDatabaseAdapter(dbAdapter);

        // Add a load listener for when Profile Manager has loaded all profiles
        pMgr.addOnLoadListener(this);
        // Can also add a load listener for eMgr to only continue once a data source has loaded in
        // eMgr.addOnLoadListener(this); // -> implement 'implements ExperimentManager.OnExperimentDataLoaded'

        eMgr.init();
        pMgr.init();
    }

    @Override
    public void onProfileDataLoaded() {
        // When profile data is loaded, allow LocalUser info to load incase we need firebase for a new account
        LocalUser lu = LocalUser.getInstance();
        lu.setContext(getApplicationContext());
        lu.loadLocalData();
    }
}