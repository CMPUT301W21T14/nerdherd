package com.example.nerdherd.UserInterfaceTrials;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Deprecated.BinomialTrialDeprecated;
import com.example.nerdherd.Deprecated.CountTrialDeprecated;
import com.example.nerdherd.Deprecated.Experiment_Deprecated;
import com.example.nerdherd.Deprecated.FireStoreController;
import com.example.nerdherd.Deprecated.MeasurementTrialDeprecated;
import com.example.nerdherd.Deprecated.NonnegativeTrialDeprecated;
import com.example.nerdherd.Deprecated.Trial_Deprecated;
import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Trial;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.TrialsListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * Trial activity in the app
 * 4 types of trial extend this class to get themselves visible on the app
 * @author Ogooluwa S. osamuel
 */

public class TrialActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    public static final int PERMISSIONS_REQUEST_LOCATION = 99;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private FloatingActionButton addtrials;
    private AdapterController adapterController;
    private TrialsListAdapter adapter;
    private RecyclerView recyclerView;
    private TrialsListAdapter.onClickListener listener;

    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private ProfileManager pMgr = ProfileManager.getInstance();
    private String experimentId;
    private Experiment experiment;
    private ArrayList<Trial> trialList;

    private Button ignoreUserBtn;
    private EditText ignoreUserEt;
    private TextView locRequiredTv;
    private TextView ignoreUserTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

        recyclerView = findViewById(R.id.list_recyclerView);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_trial_view);
        navigationView = findViewById(R.id.navigator);
        addtrials = findViewById(R.id.addTrial);
        setSupportActionBar(toolbar);

        ignoreUserBtn = findViewById(R.id.btn_ignore_experimenter);
        ignoreUserEt = findViewById(R.id.et_ignore_experimenter);
        locRequiredTv = findViewById(R.id.tv_location_required);
        ignoreUserTv = findViewById(R.id.tv_blacklist_label);


        eMgr.addOnChangeListener(this);

        menuController = new MenuController(TrialActivity.this, toolbar, navigationView, drawerLayout);

        menuController.useMenu(true);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("experimentId");
        experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            Log.d("TrialActivity", "exp=NULL");
            return;
        }

        if(!experiment.getOwnerId().equals(LocalUser.getUserId())) {
            ignoreUserBtn.setVisibility(View.GONE);
            ignoreUserEt.setVisibility(View.GONE);
            ignoreUserTv.setVisibility(View.GONE);
        }

        if(experiment.isLocationRequired()) {
            initLocationUpdates();
        } else {
            locRequiredTv.setVisibility(View.GONE);
        }

        FloatingActionButton addTrialButton = findViewById(R.id.addTrial);
        addTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(experiment.getType()) {
                    case Experiment.EXPERIMENT_TYPE_BINOMIAL:
                        new BinomialTrialDialogFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT");
                        break;
                    case Experiment.EXPERIMENT_TYPE_COUNT:
                        new CountTrialDialogFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT2");
                        break;
                    case Experiment.EXPERIMENT_TYPE_NON_NEGATIVE:
                        new NonnegativeTrialFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT2");
                        break;
                    case Experiment.EXPERIMENT_TYPE_MEASUREMENT:
                        new MeaurementTrialFragment(experimentId).show(getSupportFragmentManager(), "EDIT_TEXT3");
                        break;
                }
            }
        });

        listener = new TrialsListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if(trialList != null) {
                    Trial t = trialList.get(index);
                    if(t == null) {
                        return;
                    }
                    UserProfile up = pMgr.getProfile(t.getExperimenterId());
                    if(up == null) {
                        Log.d("TrialAct", "up=NULL");
                    }
                    ignoreUserEt.setText(up.getUserName());
                }
            }
        };

        ignoreUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eMgr.addUserToExperimentBlacklist(ignoreUserEt.getText().toString(), experimentId)) {
                    ignoreUserEt.setText("User Ignored!");
                } else {
                    invalidBlacklistUser();
                }
            }
        });

        showTrials();
    }

    // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
    public void initLocationUpdates() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            return;
        }
        beginLocationUpdates();
    }

    public void invalidBlacklistUser() {
        Toast.makeText(this, "Invalid Username", Toast.LENGTH_LONG).show();
    }

    public void addMeasurementTrial(float outcome) {
        eMgr.addTrialToExperiment(experimentId, new Trial(LocalUser.getUserId(), outcome, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addCountTrial() {
        eMgr.addTrialToExperiment(experimentId, new Trial(LocalUser.getUserId(), 1, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addNonNegativeTrial(int outcome) {
        eMgr.addTrialToExperiment(experimentId, new Trial(LocalUser.getUserId(), outcome, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addSuccessfulBinomialTrial() {
        eMgr.addTrialToExperiment(experimentId, new Trial(LocalUser.getUserId(), 1, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    public void addUnsuccessfulBinomialTrial() {
        eMgr.addTrialToExperiment(experimentId, new Trial(LocalUser.getUserId(), 0, LocalUser.getLastLocationGeo(), Timestamp.now()));
    }

    private void beginLocationUpdates() {
        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(TrialActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Can use lastLocation for the location of trials
                    // Remember to convert to a GeoPoint for firebase
                    LocalUser.setLastLocation(location);
                }
            });
        }
    }

    private void showTrials(){
        trialList = eMgr.getTrialsExcludeBlacklist(experimentId);
        RecyclerView recyclerView = findViewById(R.id.list_recyclerView);
        adapter = new TrialsListAdapter(trialList, listener);
        adapterController = new AdapterController(TrialActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        showTrials();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}