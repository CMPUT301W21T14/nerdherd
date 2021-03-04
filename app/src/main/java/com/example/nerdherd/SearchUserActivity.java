package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

public class SearchUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.user_draw_layout);
        navigationView = findViewById(R.id.user_navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchUserActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();
    }
}