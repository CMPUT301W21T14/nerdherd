package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.FirestoreAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Deprecated.LogInActivity;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.UserInterfaceSearch.SearchExperimentActivity;

/**
 * Splash screen for the app
 * The constant screen that is played for a fixed amount of time when the app is opened
 * Setup database initialization here. set useMockData=false for offline testing database
 * @author Zhipeng Z. zhipeng4, roulette
 */
public class SplashScreen extends AppCompatActivity implements ProfileManager.ProfileDataLoadedEventListener {

    private Intent logInIntent;
    private Integer time = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestPermissions();
        InitializeData(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logInIntent = new Intent(SplashScreen.this, SearchExperimentActivity.class);
                startActivity(logInIntent);
                finish();
            }
        }, time);
    }

    // TODO: maybe request all app permissions on startup
    private void RequestPermissions() {
        int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * IMPORTANT: useMockData = true to use a different mockDatabase with the program
     * below it will load a MockDatabaseAdapter instead.
     * @param useMockData
     */
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
        lu.externalPath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Log.d("PATH:", lu.externalPath.getAbsolutePath());
    }
}