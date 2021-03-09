package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class MenuController implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Intent intent;
    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MenuController(Context context, Toolbar toolbar, NavigationView navigationView, DrawerLayout drawerLayout) {
        this.context = context;
        this.toolbar = toolbar;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
    }

    public void useMenu(){
        actionBarDrawerToggle = new ActionBarDrawerToggle((Activity)context, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.user_search && !(context instanceof SearchUserActivity)){
            intent = new Intent(context, SearchUserActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiments && !(context instanceof SearchExperimentActivity)){
            intent = new Intent(context, SearchExperimentActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
        if (item.getItemId() == R.id.my_profile){
            intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.log_out){
            sharedPreferences = context.getSharedPreferences("SharedPreferences", 0);
            editor = sharedPreferences.edit();
            editor.putBoolean("Logged In", false);
            editor.apply();
            intent = new Intent(context, LogInActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
        return true;
    }
}
