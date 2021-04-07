package com.example.nerdherd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


/**
 * Controls the menu of the app and stores the input of the user
 * @author Zhipeng Z. zhipeng4
 */

public class MenuController implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Intent intent;
    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Menu menu;
    private MenuItem viewExperiments;
    private MenuItem trails;
    private String loggedInName = "Logged In";
    private String loggedInId = "User Id";
    private String userName = "User Name";
    private String userEmail = "User Email";
    private String userPassword = "User Password";
    private String userAvatar = "User Avatar";
    private String trialType;
    private int mintrial;

    /**
     * Getter/setter/constructor of the class
     * @param context stores menu details
     * @param toolbar of the menu
     * @param navigationView of the menu
     * @param drawerLayout of the menu
     */

    public MenuController(Context context, Toolbar toolbar, NavigationView navigationView, DrawerLayout drawerLayout) {
        this.context = context;
        this.toolbar = toolbar;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
    }

    public void useMenu(Boolean show){
        actionBarDrawerToggle = new ActionBarDrawerToggle((Activity)context, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        menu = navigationView.getMenu();
        viewExperiments =menu.findItem(R.id.experiment_view);
        trails = menu.findItem(R.id.experiment_trails);

        if (show) {
            trails.setVisible(true);
        }
        else{
            trails.setVisible(false);
        }


        if (context instanceof  ExperimentViewActivity || context instanceof QuestionsActivity || context instanceof TrialActivity || context instanceof statsActivity || context instanceof statsactivity_checking || context instanceof TrialsPlot || context instanceof QuestionViewActivity || context instanceof TrialsHistogram ){
            viewExperiments.setVisible(true);
        }
        else{
            viewExperiments.setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setTrialType(String type){
        trialType = type;
    }

    public void setMinTrials(int MinTrials){
        mintrial = MinTrials;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.user_search && !(context instanceof SearchUserActivity)){
            intent = new Intent(context, SearchUserActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if(item.getItemId() == R.id.qr_code) {
            intent = new Intent(context, QRCodeActivity.class);
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

        if (item.getItemId() == R.id.my_experiments && !(context instanceof MyExperimentsActivity)){
            intent = new Intent(context, MyExperimentsActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.my_subscription && !(context instanceof MySubscriptionActivity)){
            intent = new Intent(context, MySubscriptionActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.log_out){
            sharedPreferences = context.getSharedPreferences("SharedPreferences", 0);
            editor = sharedPreferences.edit();
            editor.putBoolean(loggedInName, false);
            editor.putString(userName, "");
            editor.putString(userEmail, "");
            editor.putString(userPassword, "");
            editor.putInt(userAvatar, -1);
            editor.apply();
            intent = new Intent(context, LogInActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_details && !(context instanceof ExperimentViewActivity)){
            intent = new Intent(context, ExperimentViewActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_trails && !(context instanceof TrialActivity)){
            intent = new Intent(context, TrialActivity.class);
            intent.putExtra("Type of Trial",trialType);
            intent.putExtra("Min of Trial", mintrial);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_stats && !(context instanceof statsactivity_checking)){
            intent = new Intent(context, statsactivity_checking.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        if (item.getItemId() == R.id.experiment_questions && !(context instanceof QuestionsActivity)) {
            intent = new Intent(context, QuestionsActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }


        return true;
    }
}
