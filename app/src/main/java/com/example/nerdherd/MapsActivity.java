package com.example.nerdherd;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void howToGetExperimentData() {
        // Get the intent containing the experimentId sent to this activity
        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("experimentId");

        // Get the Experiment associated with experimentId
        ExperimentManager eMgr = ExperimentManager.getInstance();
        ExperimentE experiment = eMgr.getExperiment(experimentId);

        if(experiment == null) {
            // something weng wrong
            return;
        }

        // Get all the trials associated with the experiment (non blacklisted users)
        for( TrialT trial : eMgr.getTrialsExcludeBlacklist(experimentId) ) {
            // You can probably begin mapping stuff and adding markers?
            Location loc = new Location(LocationManager.GPS_PROVIDER); // ?idk if works
            loc.setLatitude(trial.getLocation().getLatitude());
            loc.setLongitude(trial.getLocation().getLongitude());
            Timestamp date = trial.getDate();
            long timeDiff = date.getSeconds()-experiment.getDate().getSeconds();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               MarkerOptions markerOptions = new MarkerOptions();
               markerOptions.position(latLng);
               markerOptions.title(latLng.latitude + ", " + latLng.longitude);
               mMap.clear();
               mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
               mMap.addMarker(markerOptions);
            }
        });
    }
}