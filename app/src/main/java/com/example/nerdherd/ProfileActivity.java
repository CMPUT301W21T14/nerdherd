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
    //set the users name
    private TextView usersname;
    private TextView uname;
    private TextView usersemail;
    private Button edtUserProfile;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        avatar = 0;
//        getAvatar = getIntent();
//        dataBundle = getAvatar.getBundleExtra("Data");
        //get the users name
        name = GlobalVariable.profile.getName();
        email = GlobalVariable.profile.getEmail();
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

        edtUserProfile = findViewById(R.id.edt_profile);
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

    public void updateUserInfo(String Name, String Email){
        String id = GlobalVariable.profile.getId();
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }
}


