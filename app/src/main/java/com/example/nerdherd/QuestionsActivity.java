package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private RecyclerView questionListView;
    private Button buttonView;
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
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_question_view);
        navigationView = findViewById(R.id.navigator);

        experiment = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        isOwner = experiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId());

        setSupportActionBar(toolbar);
        menuController = new MenuController(QuestionsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        if (isOwner) {
            buttonView.setText(String.format("Pending Replies: %d", experiment.getPendingReplies()));
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: open pending reply dialogue
                }
            });
        }
        else {
            buttonView.setText("Ask a Question");
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: open question creation dialogue
                }
            });
        }
        listener = new QuestionsAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                // TODO: Open Question specific Activity
                Intent questionIntent = new Intent(QuestionsActivity.this, QuestionViewActivity.class);
                GlobalVariable.indexForQuestionView = index;
                startActivity(questionIntent);
            }
        };

        // TODO: get the question list from firestore
        // Test Garbage
        Question testQuestion = new Question("Does this even work?");
        Question testQuestion2 = new Question("Second in the list I actually have no replies, I am also very long and will hopefully not break something... Is this long enough?");
        Question testQuestion3 = new Question("A Third Question?");
        Reply testReply = new Reply("yes");
        Reply testReply1 = new Reply("A super long reply that will hopefully be longer than one line, long story short yes this does in fact work.");
        Reply testReply2 = new Reply("no");
        testQuestion.addReply(testReply);
        testQuestion.addReply(testReply1);
        testQuestion.addReply(testReply2);
        testQuestion.incrementReplies();
        testQuestion2.incrementReplies();
        testQuestion2.incrementReplies();
        ArrayList<Question> testList = new ArrayList<Question>();
        testList.add(testQuestion);
        testList.add(testQuestion2);
        testList.add(testQuestion3);
        GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView).questions = testList;
        listQuestions(testList);
    }

    public void listQuestions(ArrayList<Question> questions) {
        GlobalVariable.questionArrayList = questions;
        questionsAdapter = new QuestionsAdapter(questions, listener);
        AdapterController adapterController = new AdapterController(QuestionsActivity.this, questionListView, questionsAdapter);
        adapterController.useAdapter();
    }
}
