package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<Profile> profiles;
    private UserAdapter.onClickListener listener;
    private AdapterController adapterController;
    private FireStoreController fireStoreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.user_draw_layout);
        navigationView = findViewById(R.id.user_navigator);
        recyclerView = findViewById(R.id.user_recyclerView);

        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchUserActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();

        profiles = new ArrayList<Profile>();
        fireStoreController = new FireStoreController();

        listener = new UserAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                Log.d("HaHaHa","Need further activity");
            }
        };

        fireStoreController.readProfile(profiles, null, "All User", null, null, new FireStoreController.FireStoreProfileListCallback() {
            @Override
            public void onCallback(ArrayList<Profile> profileList) {
                adapter = new UserAdapter(profileList, new ProfileController().getImageArray(), listener);
                adapterController = new AdapterController(SearchUserActivity.this, recyclerView, adapter);
                adapterController.useAdapter();
            }
        }, new FireStoreController.FireStoreProfileListFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}