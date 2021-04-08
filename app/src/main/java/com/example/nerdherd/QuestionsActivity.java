package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionsActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView questionListView;
    private Button buttonView;
    private EditText questionInput;
    private QuestionsAdapter questionsAdapter;
    private QuestionsAdapter.onClickListener listener;

    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private ExperimentE experiment;
    private String experimentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        questionListView = findViewById(R.id.question_list);
        buttonView = findViewById(R.id.questions_button);
        questionInput = findViewById(R.id.question_input);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_question_view);
        navigationView = findViewById(R.id.navigator);

        eMgr.addOnChangeListener(this);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("experimentId");
        experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            Log.d("ID:", experimentId);
            Log.d("QuestionAct", "experimentId=NULL");
            return;
        }

        setSupportActionBar(toolbar);
        menuController = new MenuController(QuestionsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        buttonView.setText("Ask a Question");
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonView.setText("Confirm");
                questionInput.setVisibility(View.VISIBLE);
                buttonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String input = questionInput.getText().toString();
                        questionInput.setText("");
                        eMgr.addQuestionToExperiment(experimentId, input);
                    }
                });
            }
        });
        listener = new QuestionsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                // TODO: Open Question specific Activity
                Intent questionIntent = new Intent(QuestionsActivity.this, QuestionViewActivity.class);
                questionIntent.putExtra("experimentId", experimentId);
                questionIntent.putExtra("questionIdx", index);
                startActivity(questionIntent);
            }
        };
        listQuestions();
    }

    public void listQuestions() {
        experiment = eMgr.getExperiment(experimentId);
        if(experiment == null) {
            Log.d("ID:", experimentId);
            Log.d("QuestionAct", "experimentId=NULL");
            return;
        }
        questionsAdapter = new QuestionsAdapter(experiment.getQuestions(), listener);
        AdapterController adapterController = new AdapterController(QuestionsActivity.this, questionListView, questionsAdapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        listQuestions();
    }
}
