package com.example.nerdherd.UserInterfaceSearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Trial;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.ExperimentListAdapter;
import com.example.nerdherd.UserInterfaceExperiments.CreateExperimentActivity;
import com.example.nerdherd.UserInterfaceExperiments.ExperimentViewActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

/**
 * This handles when a user wants to search a specific experiment with keyword
 * @author Utkarsh S. usaraswa
 * @author Zhipeng Z. zhipeng4
 * @author Harjot S. harjotsi
 */
public class SearchExperimentActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ExperimentListAdapter.onClickListener listener;
    private MenuController menuController;
    private AdapterController adapterController;
    private ExperimentListAdapter adapter;
    private Intent experimentView;
    private TextView keywordView;
    private Button searchButton;
    private long backPressedTime;
    private Toast backToast;
    private ExperimentManager eMgr = ExperimentManager.getInstance();

    private ArrayList<Experiment> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experiment);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);
        keywordView = findViewById(R.id.keyword_edit);
        searchButton = findViewById(R.id.search_button);

        eMgr.addOnChangeListener(this);

        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchExperimentActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperiments();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.experiment_recyclerView);
        listener = new ExperimentListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                experimentView = new Intent(SearchExperimentActivity.this, ExperimentViewActivity.class);
                Experiment e = searchResult.get(index);
                String experimentId = e.getExperimentId();
                experimentView.putExtra("experimentId", experimentId);
                Log.d("Clicked Exp: ", e.getDescription());
                startActivity(experimentView);
                //finish(); //clicking on back after selecting an experiment doesn't exit the app
            }
        };

        showExperiments();
    }

    @Override
    protected void onDestroy() {
        eMgr.removeOnChangeListener(this);
        super.onDestroy();
    }

    // this sees if the user has pressed back button twice within 2 seconds to exit the app
    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    // Start the CreateExperiment Activity on button press
    public void createExperimentButton(View view) {
        Intent createExpIntent = new Intent(SearchExperimentActivity.this, CreateExperimentActivity.class);
        startActivity(createExpIntent);
    }

    private void showExperiments(){
        String keyword = keywordView.getText().toString();
        keywordView.setText("");
        searchResult = eMgr.searchForExperimentsByKeyword(keyword);
        RecyclerView recyclerView = findViewById(R.id.experiment_recyclerView);
        adapter = new ExperimentListAdapter(searchResult, listener);
        adapterController = new AdapterController(SearchExperimentActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        showExperiments();
    }
}