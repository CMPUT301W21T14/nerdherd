package com.example.nerdherd;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

/**
 * Location marker placer
 * Will put the current location in the database initiating the trial
 * The marker states the current location of the user who is doing the experiment
 */

public class StartTrialActivity extends Activity {
    private SupportMapFragment smf;
    private FusedLocationProviderClient client;
    private String experimentTitle;
    private MaterialButton goBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trial);
        goBackBtn = findViewById(R.id.gobackBtn);

        experimentTitle = getIntent().getStringExtra("expTitle");

        isGpsEnabled(StartTrialActivity.this);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
