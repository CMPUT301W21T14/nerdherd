package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

public class ExperimentViewActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_view);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ExperimentViewActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();
    }
}