package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Intent searchUser;
    private Intent UsersProfile;
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
    private ProfileController profController;
    private FireStoreController fireStoreController;
    //set the users name
    private TextView usersname;
    private TextView uname;
    private TextView usersemail;
    private Button edtUserProfile;
    private Intent publicuser;
    private int val;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edtUserProfile = findViewById(R.id.edt_profile);
        name = GlobalVariable.profile.getName();
        email = GlobalVariable.profile.getEmail();
        publicuser = getIntent();
        Bundle pUser = publicuser.getExtras();

        //if pUser is null then it is a user(current logged in user or public user)
        // that needs to be displayed
        if (pUser != null)
        {

            Intent intent = getIntent();
            val = intent.getIntExtra(SearchUserActivity.EXTRA_MESSAGE, -1);
            name = GlobalVariable.profileArrayList.get(val).getName();
            email = GlobalVariable.profileArrayList.get(val).getEmail();
            //if the public name matches up with the private name
            //or if the searched user == the logged in user
            //then set the button to be visible (give functionality) to edit your profile
            // else remove the button
            if (name.equals(GlobalVariable.profile.getName()))
            {
                edtUserProfile.setVisibility(View.VISIBLE);
            }
            else{
                edtUserProfile.setVisibility(View.GONE);
            }
//            Log.d("MYINT", "publicname: "+ sp);
        }



        //if no value is stored -> -1
        //else: it returns the index
//        GlobalVariable.profileArrayList.get(val).getName();
//        if (pUser!= null)
//        {
//            publicName = pUser.getString("Index");
//            Log.d("Userval", publicName);
//        }
//        String Name = GlobalVariable.profileArrayList.get(publicName).getName();


//        avatar = 0;
//        getAvatar = getIntent();
//        dataBundle = getAvatar.getBundleExtra("Data");
        //get the users name

        fireStoreController = new FireStoreController();
//        if (dataBundle != null) {
//            avatar = dataBundle.getInt("Index", 0);
//            name = dataBundle.getString("Name");
//            password = dataBundle.getString("Password");
//            email = dataBundle.getString("Email");
//        }
        profController= new ProfileController();
        Log.d("Test", GlobalVariable.profile.getName());
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.navigator);
        usersAvatar = findViewById(R.id.avatarEdit2);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        usersname = findViewById(R.id.UsersName);
        usersname.setText(name+"");


        usersemail = findViewById(R.id.usersEmail);
        usersemail.setText(email+"");

        uname = findViewById(R.id.Name);
        uname.setText(name+"");


        //TODO: needs to fix to implement controller as opposed to model(profile class)
        avatar = GlobalVariable.profile.getAvatar();
        navigationView.setNavigationItemSelectedListener(this);

        edtUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfileFragment().show(getSupportFragmentManager(), "EDIT_PROFILE");
            }
        });

//        usersImg.setImageResource(R.drawable.zelda);
        usersAvatar.setImageResource(profController.getImageArray().get(avatar));

        //set the users name

//        usersAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dataBundle = new Bundle();
//                dataBundle.putString("Name", GlobalVariable.profile.getName());
//                avatarPicker = new Intent(profileActivity.this, AvatarPicker.class);
//                avatarPicker.putExtra("Data", dataBundle);
//                startActivity(avatarPicker);
//                finish();
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.user_search){
            searchUser = new Intent(ProfileActivity.this, SearchUserActivity.class);
            startActivity(searchUser);
            finish();
        }
        if (item.getItemId() == R.id.my_profile){
            UsersProfile = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(UsersProfile);
            finish();
        }
        return true;
    }

    //This method responsible for interacting with controller which calls
    //profile to get the users id, After changes made by the user (Edit their information)
    //the firestoreController updates their information and returns 'success' or 'failure'
    public void updateUserInfo(String Name, String Email){
        String id = profController.getId();
        fireStoreController.updater(id, "Name", Name, new FireStoreController.FireStoreUpdateCallback(){
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), " Successfully updated.", Toast.LENGTH_SHORT).show();
                uname.setText(Name+"");
            }
        }, new FireStoreController.FireStoreUpdateFailCallback(){
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


