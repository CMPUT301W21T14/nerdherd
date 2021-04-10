package com.example.nerdherd.UserInterfaceExperiments;

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

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.ExperimentListAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * Shows all the experiments the logged in user is currently subscribed to
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */
public class MySubscriptionActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ExperimentListAdapter.onClickListener listener;
    private MenuController menuController;
    private AdapterController adapterController;
    private ExperimentListAdapter adapter;
    private TextView keywordView;
    private Button searchButton;
    private ArrayList<Experiment> experiments;
    private RecyclerView recyclerView;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscription);


        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_mySubscription);
        navigationView = findViewById(R.id.navigator);
        keywordView = findViewById(R.id.subscription_keyword_edit);
        searchButton = findViewById(R.id.subscription_search_button);


        setSupportActionBar(toolbar);

        menuController = new MenuController(MySubscriptionActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        recyclerView = findViewById(R.id.subscription_experiment_recyclerView);
        listener = new ExperimentListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if(experiments != null) {
                    Experiment e = experiments.get(index);
                    if(e == null) {
                        return;
                    }

                    Log.d("Clicked Exp: ", e.getDescription());

                    Intent experimentView = new Intent(MySubscriptionActivity.this, ExperimentViewActivity.class);
                    experimentView.putExtra("experimentId", e.getExperimentId());

                    startActivity(experimentView);
                    //finish(); //clicking back takes us to the previous activity
                }
            }
        };

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExperiments();
            }
        });

        showExperiments();
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

    private void showExperiments() {
        experiments = new ArrayList<>();
        ExperimentManager eMgr = ExperimentManager.getInstance();
        for(String experimentId : LocalUser.getSubscribedExperiments()) {
            Experiment e = eMgr.getExperiment(experimentId);
            Log.d("Subscription: ", experimentId);
            if(e == null) {
                // Invalid - just ignore it I guess
                Log.d("Null", experimentId);
                continue;
            }
            if(!e.isPublished() && !e.getOwnerId().equals(LocalUser.getUserId())) {
                continue;
            }

            if(!eMgr.experimentContainsKeyword(e, keywordView.getText().toString()))  {
                continue;
            }
            experiments.add(e);
        }
        adapter = new ExperimentListAdapter(experiments, listener);
        adapterController = new AdapterController(MySubscriptionActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }

}