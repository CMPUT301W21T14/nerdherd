package com.example.nerdherd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Profile activity in the app
 * Profile is unique per user with different parameters
 * Currently logged in user/owner would see things differently
 * @author Ogooluwa S. osamuel
 * @author Zhipeng Z. zhipeng4
 */

public class ProfileActivity extends AppCompatActivity implements ProfileManager.ProfileOnChangeEventListener {

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
    private TextView userExperiments;
    private Button edtUserProfile;
    private Intent publicuser;
    private int val;
    private String useridval;
    private ArrayList<String> updateditem;
    private String CurrentName;
    private long backPressedTime;
    private Toast backToast;

    private ProfileManager pMgr = ProfileManager.getInstance();
    private ExperimentManager eMgr = ExperimentManager.getInstance();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Experiment experiment;
    private Button usersExpDetailed;
    private ArrayList<Experiment> savedList;

    private ArrayList<Experiment> allExperiments;
    private ArrayList<Experiment> returneditems;
    private ArrayList<Experiment> newExperiment;

    private int current_exp;
    private int publicUSer_exp;
    private ArrayList<Experiment> revealList;
    private ArrayList<Experiment> savedList2;
    private int publicExpCounter;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(ProfileActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        usersExpDetailed = findViewById(R.id.more_info);
        userExperiments = findViewById(R.id.expOwned);
        edtUserProfile = findViewById(R.id.edt_profile);
        usersname = findViewById(R.id.UsersName);
        usersemail = findViewById(R.id.usersEmail);
        uname = findViewById(R.id.Name);
        usersAvatar = findViewById(R.id.avatarEdit2);

        pMgr.addOnChangeListener(this);

        updateViews();

        edtUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileFragment().show(getSupportFragmentManager(), "EDIT_TEXT2");
            }
        });

        usersExpDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyExperimentsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateViews() {
        UserProfile up = pMgr.getProfile(LocalUser.getUserId());
        if(up == null) {
            Log.d("Null userId", "very bad");
            // shouldn't happen
            return;
        }

        int numExperiments = eMgr.getOwnedExperiments().size();
        userExperiments.setText(numExperiments+""+" Experiments Currently Owned");
        uname.setText(up.getUserName());
        usersname.setText(up.getUserName());
        usersemail.setText(up.getContactInfo());
        usersAvatar.setImageResource(LocalUser.imageArray.get(up.getAvatarId()));
    }

        /*
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
                    savedList = new ArrayList<Experiment>();
                    fireStoreController = new FireStoreController();

                    fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
                        @Override
                        public void onCallback(ArrayList<Experiment> experiments) {
                            savedList.clear();
                            Log.d("SavedList", savedList.toString());
                            publicExpCounter = 0;
                            Log.d("Experiments", experiments.toString());
                            for (int counter = 0; counter < experiments.size(); counter++) {
                                Log.d("current user id: ", id);
                                if (id.equals(experiments.get(counter).getOwnerProfile().getId())) {
                                    Log.d("experiments Owned", "-------------");
                                    publicExpCounter+=1;
                                }
                            }
                            Log.d("current public count", String.valueOf(publicExpCounter));
                            userExperiments.setText(publicExpCounter+""+" Experiments Currently Owned");


                        }
                    }, new FireStoreController.FireStoreExperimentReadFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ProfileUpdate();
                    getDescription();
                } else {
                    //represents the experiments of different user
                    usersAvatar.setImageResource(profController.getImageArray().get(avatar));
                    getDescription();
                    edtUserProfile.setVisibility(View.GONE);
                    usersExpDetailed.setVisibility(View.GONE);

                    savedList.clear();
                    fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
                        @Override
                        public void onCallback(ArrayList<Experiment> experiments) {
                            Log.d("SavedList", savedList.toString());
                            publicExpCounter = 0;
                            Log.d("Experiments", experiments.toString());
                            for (int counter = 0; counter < experiments.size(); counter++) {
                                Log.d("current user id: ", id);

                                if (id.equals(experiments.get(counter).getOwnerProfile().getId())) {
                                    publicExpCounter+=1;
                                }
                            }


                        }
                    }, new FireStoreController.FireStoreExperimentReadFailCallback() {
                        @Override
                        public void onCallback() {
                            Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    });
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
            getDescription();
            savedList.clear();
            id = GlobalVariable.profile.getId();
            fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
                @Override
                public void onCallback(ArrayList<Experiment> experiments) {
                    Log.d("SavedList", savedList.toString());
                    int publicExpCounters = 0;
                    Log.d("Experiments", experiments.toString());
                    for (int counter = 0; counter < experiments.size(); counter++) {
                        Log.d("current user id: ", id);
                        if (id.equals(experiments.get(counter).getOwnerProfile().getId())) {
                            publicExpCounters+=1;
                        }
                    }
                    userExperiments.setText(publicExpCounters+""+" Experiments Currently Owned");


                }
            }, new FireStoreController.FireStoreExperimentReadFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("hereee", "-------------------------------");
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
        usersAvatar.setImageResource(profController.getImageArray().get(avatar));


        edtUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileFragment(avatar).show(getSupportFragmentManager(), "EDIT_TEXT2");
            }
        });

        usersExpDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyExperimentsActivity.class);
                startActivity(intent);
            }
        });
    }*/

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

    @Override
    public void onProfileDataChanged() {
        updateViews();
    }


    /*public void getDescription() {
        current_exp = 0;
        savedList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        returneditems = new ArrayList<Experiment>();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {
                current_exp = 0;
                for (int counter = 0; counter < experiments.size(); counter++) {
//                for (Experiment allExperiment:experiments) {
                    if (experiments.get(counter).getOwnerProfile().getId().equals(GlobalVariable.profile.getId())) {
                        current_exp+=1;
//                    savedList.add(allExperiment);
//                    returneditems.add(allExperiment);
                    }

                }
                Log.d("Unique", String.valueOf(current_exp));
                userExperiments.setText(current_exp+""+" Experiments Currently Owned");
            }

        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
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
                    fireStoreController.updater("Profile", useridval, "Email", Email, new FireStoreController.FireStoreUpdateCallback() {
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
            fireStoreController.updater("Profile", useridval, "Name", Name, new FireStoreController.FireStoreUpdateCallback() {
                @Override
                public void onCallback() {
                    uname.setText(Name+"");
                    usersname.setText(Name+"");
                    Log.d("Experiments owned", String.valueOf(GlobalVariable.experimentArrayList.get(0).getTitle()));
                    GlobalVariable.profile.setName(Name);


                }
            }, new FireStoreController.FireStoreUpdateFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!Password.isEmpty()){
            fireStoreController.updater("Profile", useridval, "Password", Password, new FireStoreController.FireStoreUpdateCallback() {
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
            fireStoreController.updater("Profile", useridval, "Avatar", avatar.toString(), new FireStoreController.FireStoreUpdateCallback() {
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
            ExperimentLinkage(Name, Email, avatar);
            getDescription();
        }
    }

    public void ExperimentLinkage(String Name, String email, Integer avatar){

        newExperiment = new ArrayList<Experiment>();
        useridval = profController.getId();
        String passWord = profController.getPassword();
        Profile profile = new Profile(Name, passWord, email, useridval, avatar);
        allExperiments = GlobalVariable.experimentArrayList;
        for (int y =0 ; y < allExperiments.size(); y++){
            if (allExperiments.get(y).getOwnerProfile().getId().equals(GlobalVariable.profile.getId())){
                fireStoreController.updater("Experiment", allExperiments.get(y).getTitle(), "Owner Profile", profile, new FireStoreController.FireStoreUpdateCallback() {
                    @Override
                    public void onCallback() {
//                uname.setText(Name+"");
//                usersname.setText(Name+"");
//                Log.d("Experiments owned", String.valueOf(GlobalVariable.experimentArrayList.get(0).getTitle()));
//                GlobalVariable.profile.setName(Name);

                    }
                }, new FireStoreController.FireStoreUpdateFailCallback() {
                    @Override
                    public void onCallback() {
                        Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }*/


}


