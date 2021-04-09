package com.example.nerdherd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.ObjectManager.ExperimentManager;
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
        setContentView(R.layout.heatmap);
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
        ExperimentE experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            return;
        }
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        for( TrialT t : experiment.getTrials() ) {
            if(t.getLocation() != null) {
                latLngArrayList.add(new LatLng(t.getLocation().getLatitude(), t.getLocation().getLongitude()));
            }
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(latLngArrayList).build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
    /**
     * Read the data (locations of police stations) from raw resources.
     */
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        @SuppressWarnings("resource")
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }

        return list;
    }
}