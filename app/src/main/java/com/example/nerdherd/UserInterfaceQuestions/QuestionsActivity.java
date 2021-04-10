package com.example.nerdherd.UserInterfaceQuestions;

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

import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.QuestionsListAdapter;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity to ask a question and select questions to reply to
 */
public class QuestionsActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView questionListView;
    private Button buttonView;
    private EditText questionInput;
    private QuestionsListAdapter questionsAdapter;
    private QuestionsListAdapter.onClickListener listener;

    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private Experiment experiment;
    private String experimentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // US 02.01.01
        // As an experimenter, I want to ask a question about an experiment.
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

        buttonView.setText("Confirm");
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = questionInput.getText().toString();
                questionInput.setText("");
                eMgr.addQuestionToExperiment(experimentId, input);
            }
        });
        listener = new QuestionsListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
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
        questionsAdapter = new QuestionsListAdapter(experiment.getQuestions(), listener);
        AdapterController adapterController = new AdapterController(QuestionsActivity.this, questionListView, questionsAdapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        listQuestions();
    }
}
