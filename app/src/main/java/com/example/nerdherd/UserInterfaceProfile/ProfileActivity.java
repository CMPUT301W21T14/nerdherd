package com.example.nerdherd.UserInterfaceProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;
import com.example.nerdherd.UserInterfaceExperiments.MyExperimentsActivity;
import com.google.android.material.navigation.NavigationView;

/**
 * Profile activity in the app
 * Profile is unique per user with different parameters
 * Currently logged in user/owner would see things differently
 * @author Ogooluwa S. osamuel
 * @author Zhipeng Z. zhipeng4
 */
public class ProfileActivity extends AppCompatActivity implements ProfileManager.ProfileOnChangeEventListener {

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private Toolbar toolbar;

    private ImageView usersAvatar;

    public  String email;

    private MenuController menuController;

    //set the users name
    private TextView usersname;
    private TextView uname;
    private TextView usersemail;
    private TextView userExperiments;
    private Button edtUserProfile;

    private long backPressedTime;
    private Toast backToast;

    private ProfileManager pMgr = ProfileManager.getInstance();
    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private UserProfile currentProfile;

    private Button usersExpDetailed;

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
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        currentProfile = pMgr.getProfile(userId);
        if(currentProfile == null) {
            Log.d("Null userId", "very bad");
            // shouldn't happen
            return;
        }

        int numExperiments = eMgr.getOwnedExperiments().size();
        userExperiments.setText(numExperiments+""+" Experiments Currently Owned");
        uname.setText(currentProfile.getUserName());
        usersname.setText(currentProfile.getUserName());
        usersemail.setText(currentProfile.getContactInfo());
        usersAvatar.setImageResource(LocalUser.imageArray.get(currentProfile.getAvatarId()));

        if(!userId.equals(LocalUser.getUserId())) {
            edtUserProfile.setVisibility(View.GONE);
            usersExpDetailed.setVisibility(View.GONE);
        }
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

    @Override
    public void onProfileDataChanged() {
        updateViews();
    }
}


