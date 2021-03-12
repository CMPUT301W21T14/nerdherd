package com.example.nerdherd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Intent searchUser;
    private Intent UsersProfile;
    private Intent Experiments;
    private Intent Subscription;
    private Intent myExperiments;
    private Bundle dataBundle;
    //the users custom image
    private ImageView usersAvatar;
    private Intent avatarPicker;
    private Integer avatar;
    private Intent getAvatar;
    private String name;
    private String password;
    public  String email;
    private String id;
    //profile controller
    private ArrayList<String> emailData;
    private Boolean isNew;
    private ProfileController profController;
    private MenuController menuController;
    private FireStoreController fireStoreController;
    //set the users name
    private TextView usersname;
    private TextView uname;
    private TextView usersemail;
    private Button edtUserProfile;
    private Intent publicuser;
    private int val;
    private String useridval;
    private ArrayList<String> updateditem;
    private String CurrentName;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ProfileActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu();


        edtUserProfile = findViewById(R.id.edt_profile);
        name = GlobalVariable.profile.getName();
        email = GlobalVariable.profile.getEmail();
        usersAvatar = findViewById(R.id.avatarEdit2);
        publicuser = getIntent();
        Bundle pUser = publicuser.getExtras();
        profController= new ProfileController();
        usersname = findViewById(R.id.UsersName);
        usersemail = findViewById(R.id.usersEmail);
        uname = findViewById(R.id.Name);
        avatar = GlobalVariable.profile.getAvatar();
        //if pUser is not null then this was instantiated by the searchUserActivity
        if (pUser != null)
        {

            Intent intent = getIntent();
            val = intent.getIntExtra(SearchUserActivity.EXTRA_MESSAGE, -1);

            if (val != -1) {
                name = GlobalVariable.profileArrayList.get(val).getName();
                email = GlobalVariable.profileArrayList.get(val).getEmail();
                avatar = GlobalVariable.profileArrayList.get(val).getAvatar();
                Log.d("public avatar", String.valueOf(avatar));
                Log.d("Private avatar", String.valueOf(GlobalVariable.profile.getAvatar()));
                id = GlobalVariable.profileArrayList.get(val).getId();
                //if the public name matches up with the private name
                //or if the searched user == the logged in user
                //then set the button to be visible (give functionality) to edit your profile
                // else remove the button
                if (id.equals(GlobalVariable.profile.getId())) {
                    edtUserProfile.setVisibility(View.VISIBLE);
                    ProfileUpdate();
                } else {
                    usersAvatar.setImageResource(profController.getImageArray().get(avatar));
                    edtUserProfile.setVisibility(View.GONE);

                }
            }
            else{
                new EditProfileFragment(avatar).show(getSupportFragmentManager(), "EDIT_TEXT2");
            }
        }
        //if pUser is null -that would mean this activity was not instantiated by
        //searchUserActivity class
        else{
            ProfileUpdate();
        }



        Log.d("Test", GlobalVariable.profile.getName());
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        Log.d("Current Name", name);
        usersname.setText(name+"");
        usersemail.setText(email+"");
        uname.setText(name+"");


        edtUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileFragment(avatar).show(getSupportFragmentManager(), "EDIT_TEXT2");
            }
        });

    }

    public void ProfileUpdate(){
        fireStoreController = new FireStoreController();
        fireStoreController.readProfile(null, profController.getId(), "Current User", new FireStoreController.FireStoreProfileCallback() {
            @Override
            public void onCallback(String name, String password, String email, Integer avatar) {
                usersname.setText(name+"");
                usersemail.setText(email+"");
                uname.setText(name+"");
                usersAvatar.setImageResource(profController.getImageArray().get(avatar));
            }
        }, new FireStoreController.FireStoreProfileFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        },null, null);
    }
    public void updateEmail(String Email){
        isNew = true;
        useridval = profController.getId();
        emailData = new ArrayList<String>();
        fireStoreController.readData(emailData, "Email", new FireStoreController.FireStoreReadCallback() {
            @Override
            public void onCallback(ArrayList<String> list) {
                for (String usedEmail : list) {
                    if (usedEmail.equals(useridval)) {
                        isNew = false;
                    }
                }
                if (isNew) {
                    fireStoreController.updater(useridval, "Email", Email, new FireStoreController.FireStoreUpdateCallback() {
                        @Override
                        public void onCallback() {
                            usersemail.setText(Email+"");
                        }
                    }, new FireStoreController.FireStoreUpdateFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "The email address is already used. Thank you.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new FireStoreController.FireStoreReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    public void updateUserProfile(String Name, String Email, String Password, Integer avatar) {
        fireStoreController = new FireStoreController();
        useridval = profController.getId();
        if (!Email.isEmpty()){
            updateEmail(Email);
        }
        if (!Name.isEmpty()){
            fireStoreController.updater(useridval, "Name", Name, new FireStoreController.FireStoreUpdateCallback() {
                @Override
                public void onCallback() {
                    uname.setText(Name+"");
                    usersname.setText(Name+"");

                }
            }, new FireStoreController.FireStoreUpdateFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!Password.isEmpty()){
            fireStoreController.updater(useridval, "Password", Password, new FireStoreController.FireStoreUpdateCallback() {
                @Override
                public void onCallback() {

                }
            }, new FireStoreController.FireStoreUpdateFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (avatar != -1){
            fireStoreController.updater(useridval, "Avatar", avatar.toString(), new FireStoreController.FireStoreUpdateCallback() {
                @Override
                public void onCallback() {
                    GlobalVariable.profile.setAvatar(avatar);
                    sharedPreferences = getSharedPreferences("SharedPreferences",0);
                    editor = sharedPreferences.edit();
                    editor.putInt("User Avatar", avatar);
                    editor.apply();
                    usersAvatar.setImageResource(profController.getImageArray().get(avatar));
                }
            }, new FireStoreController.FireStoreUpdateFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!Password.isEmpty() | !Name.isEmpty() | !Email.isEmpty() | avatar != -1){
            Toast.makeText(getApplicationContext(), "User Profile Updated", Toast.LENGTH_SHORT).show();
        }
    }

}


