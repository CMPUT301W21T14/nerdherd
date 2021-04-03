package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView questionListView;
    private Button buttonView;
    private EditText questionInput;
    private Experiment experiment;
    private boolean isOwner;
    private QuestionsAdapter questionsAdapter;
    private QuestionsAdapter.onClickListener listener;

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

        experiment = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        isOwner = experiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId());

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
                        Question newQuestion = new Question(input);
                        experiment.addQuestion(newQuestion);
                        saveExperimentQuestions();
                    }
                });
            }
        });
        listener = new QuestionsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                // TODO: Open Question specific Activity
                Intent questionIntent = new Intent(QuestionsActivity.this, QuestionViewActivity.class);
                GlobalVariable.indexForQuestionView = index;
                startActivity(questionIntent);
                listQuestions();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        listQuestions();
    }

    public void saveExperimentQuestions() {
        // This saves the entire experiments 'Questions' field over again
        ArrayList<HashMap> data = new ArrayList<>();                // Array list of hashmaps
        for( int i=0;i<experiment.getQuestions().size();i++ ) {     // Find all our questions (elements of data)
            HashMap<Integer, HashMap> question = new HashMap<>();  // This is an array element of data
            Question q = experiment.getQuestions().get(i);          // Current question

            HashMap<String, Object> qdata = new HashMap<>();        // Question data (content: question, replies: arraylist<string>

            qdata.put("content", q.getContent());                   // qdata.content = question

            ArrayList<String> rdata = new ArrayList<>();            // rdata = qdata.replies

            for( int j=0; j<q.getReplies().size();++j) {            // for each reply in question
                String reply = q.getReplies().get(j).getContent();
                rdata.add(reply);
            }
            qdata.put("replies", rdata);                            //This question now has all of it's replies
            data.add(qdata);                                //This question now gets put into the question map [0], qdata
        }
        FireStoreController fireStoreController = new FireStoreController();
        fireStoreController.updater("Experiment", experiment.getTitle(), "Questions", data, new FireStoreController.FireStoreUpdateCallback() {
            @Override
            public void onCallback() {
                // successfully updated in the database
                Toast.makeText(getApplicationContext(), "New question asked", Toast.LENGTH_LONG).show();
            }
        }, new FireStoreController.FireStoreUpdateFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listQuestions() {
        questionsAdapter = new QuestionsAdapter(experiment.getQuestions(), listener);
        AdapterController adapterController = new AdapterController(QuestionsActivity.this, questionListView, questionsAdapter);
        adapterController.useAdapter();
    }
}
