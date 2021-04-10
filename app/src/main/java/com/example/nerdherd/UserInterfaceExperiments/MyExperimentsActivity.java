package com.example.nerdherd.UserInterfaceExperiments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.MyExperimentListAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * The current logged in user sees his experiments for the app to store
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osameul
 */
public class MyExperimentsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private MyExperimentListAdapter.onClickListener listener;
    private ArrayList<Experiment> experimentList;
    private AdapterController adapterController;
    private MyExperimentListAdapter adapter;

    private long backPressedTime;
    private Toast backToast;
    RecyclerView recyclerView;
    private ExperimentManager eMgr = ExperimentManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experiments);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.profileExp_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(MyExperimentsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        recyclerView = findViewById(R.id.experimentprofile_recyclerView);
        listener = new MyExperimentListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                Experiment e = experimentList.get(index);
                Intent intent = new Intent(MyExperimentsActivity.this, ExperimentViewActivity.class);
                intent.putExtra("experimentId", e.getExperimentId());
                startActivity(intent);
            }
        };

        showExperiments();
    }

    private void showExperiments() {
        experimentList = eMgr.getOwnedExperiments();
        adapter = new MyExperimentListAdapter(experimentList, listener);
        adapterController = new AdapterController(MyExperimentsActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
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
}