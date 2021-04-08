package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.RecycleViewAdapters.ProfileListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

/**
 * This handles when a user wants to search a specific user with keyword
 * @author Utkarsh S. usaraswa
 * @author Zhipeng Z. zhipeng4
 * @author Harjot S. harjotsi
 */

public class SearchUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView recyclerView;
    private ProfileListAdapter adapter;
    private ArrayList<Profile> profiles;
    private ProfileListAdapter.onClickListener listener;
    private AdapterController adapterController;
    private FireStoreController fireStoreController;
    private Button searchButton;
    private TextView keywordEdit;
    private String keyword;
    private ArrayList<UserProfile> resultList;
    private Intent profileView;
    private SearchController searchController;
    private ProfileManager pMgr = ProfileManager.getInstance();
    public static final String EXTRA_MESSAGE = "userId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.user_draw_layout);
        navigationView = findViewById(R.id.user_navigator);
        recyclerView = findViewById(R.id.user_recyclerView);
        searchButton = findViewById(R.id.user_search_button);
        keywordEdit = findViewById(R.id.user_keyword_edit);

        setSupportActionBar(toolbar);

        menuController = new MenuController(SearchUserActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfiles();
            }
        });

        listener = new ProfileListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                UserProfile up = resultList.get(index);
                if(up == null) {
                    // shouldn't happen
                    return;
                }

                profileView = new Intent(SearchUserActivity.this, ProfileActivity.class);
                profileView.putExtra(EXTRA_MESSAGE, up.getUserId());
                startActivity(profileView);
                //finish(); //finish was taken to fix the back button to go back to user screen instead of closing the app
            }
        };
        showProfiles();



        /*fireStoreController.readProfile(profiles, null, "All User", null, null, new FireStoreController.FireStoreProfileListCallback() {
            @Override
            public void onCallback(ArrayList<Profile> profileList) {
                showProfiles(profileList);
                GlobalVariable.profileArrayList = profileList;

                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resultList = new ArrayList<Profile>();
                        keyword = keywordEdit.getText().toString();
                        searchController = new SearchController();
                        searchController.searchUser(keyword, profileList, resultList, new SearchController.UserNoResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Profile> itemList) {
                                new AlertDialog.Builder(SearchUserActivity.this).setTitle("No Result").setMessage("No result found. Please enter another keyword. Thank you.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                showProfiles(itemList);
                                                GlobalVariable.profileArrayList = itemList;
                                            }
                                        }).show();
                            }
                        }, new SearchController.UserResultCallBack() {
                            @Override
                            public void onCallback(ArrayList<Profile> itemList) {
                                showProfiles(itemList);
                                GlobalVariable.profileArrayList = itemList;
                            }
                        }, new SearchController.UserNoKeywordCallBack() {
                            @Override
                            public void onCallback(ArrayList<Profile> itemList) {
                                showProfiles(itemList);
                                GlobalVariable.profileArrayList = itemList;
                            }
                        });
                    }
                });
            }
        }, new FireStoreController.FireStoreProfileListFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void showProfiles(){
        resultList = pMgr.searchProfileByKeyword(keywordEdit.getText().toString());
        adapter = new ProfileListAdapter(resultList, listener);
        adapterController = new AdapterController(SearchUserActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }
}