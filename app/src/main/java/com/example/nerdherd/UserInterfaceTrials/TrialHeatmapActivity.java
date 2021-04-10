package com.example.nerdherd.UserInterfaceTrials;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Trial;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import androidx.fragment.app.FragmentActivity;

/** Modified from
 * https://github.com/sarahmaddox/simple-heatmap/blob/master/simple-heatmap/src/com/example/simple_heatmap/MainActivity.java
 */
public class TrialHeatmapActivity extends FragmentActivity {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.trial_heatmap_activity);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Grabbing the new onMapReady
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Get the map
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.18887666666667, -122.18766499999998), 12));
                    addHeatMap();
                }
            });
        }
    }
    /**
     * Add a simple heat map to the map
     */
    private void addHeatMap() {
        ExperimentManager eMgr = ExperimentManager.getInstance();
        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("experimentId");
        Experiment experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            return;
        }
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        for( Trial t : experiment.getTrials() ) {
            if(t.getLocation() != null) {
                latLngArrayList.add(new LatLng(t.getLocation().getLatitude(), t.getLocation().getLongitude()));
            }
        }
        if(latLngArrayList.isEmpty()) {
            setResult(5);
            finish();
            return;
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(latLngArrayList).build();
        // provider.setRadius - maybe make dots bigger?
        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
}