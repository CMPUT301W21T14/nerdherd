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
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Base activity for viewing the replies on a particular Question
 */
public class QuestionViewActivity extends AppCompatActivity implements ExperimentManager.ExperimentOnChangeEventListener {


    private RecyclerView replyListView;
    private Button replyButton;
    private EditText replyInput;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ExperimentE experiment;
    private boolean isOwner;
    private ReplyAdapter replyAdapter;
    private ExperimentManager eMgr = ExperimentManager.getInstance();
    private int questionIdx;
    private String experimentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        eMgr.addOnChangeListener(this);

        replyListView = findViewById(R.id.reply_list);
        replyButton = findViewById(R.id.add_reply_button);
        replyInput = findViewById(R.id.reply_input);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_replies_view);
        navigationView = findViewById(R.id.navigator);
        TextView questionContent = findViewById(R.id.question_text);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("experimentId");
        experiment = eMgr.getExperiment(experimentId);
        questionIdx = intent.getIntExtra("questionIdx", -1);
        if(questionIdx == -1 || experiment == null) {
            Log.d("QuestionReply", "InvalidIdxOrExp=Null");
            return;
        }

        isOwner = experiment.getOwnerId().equals(LocalUser.getUserId());

        setSupportActionBar(toolbar);
        MenuController menuController = new MenuController(QuestionViewActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        replyButton.setText("Confirm");
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = replyInput.getText().toString();
                replyInput.setText("");
                eMgr.addReplyToExperimentQuestion(experimentId, questionIdx, input);
            }
        });
        questionContent.setText(experiment.getQuestions().get(questionIdx).getContent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        listReplies();
    }

    public void listReplies() {
        experiment = eMgr.getExperiment(experimentId);
        if(questionIdx == -1 || experiment == null) {
            Log.d("QuestionReply", "InvalidIdxOrExp=Null");
            return;
        }
        replyAdapter = new ReplyAdapter(experiment.getQuestions().get(questionIdx).getReplies());
        AdapterController adapterController = new AdapterController(QuestionViewActivity.this, replyListView, replyAdapter);
        adapterController.useAdapter();
    }

    @Override
    public void onExperimentDataChanged() {
        listReplies();
    }
}