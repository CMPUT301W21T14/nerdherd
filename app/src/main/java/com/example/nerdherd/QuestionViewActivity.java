package com.example.nerdherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class QuestionViewActivity extends AppCompatActivity {

    private RecyclerView replyListView;
    private Button replyButton;
    private EditText replyInput;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Experiment experiment;
    private boolean isOwner;
    private ReplyAdapter replyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        replyListView = findViewById(R.id.reply_list);
        replyButton = findViewById(R.id.add_reply_button);
        replyInput = findViewById(R.id.reply_input);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_replies_view);
        navigationView = findViewById(R.id.navigator);
        TextView questionContent = findViewById(R.id.question_text);

        experiment = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        isOwner = experiment.getOwnerProfile().getId().equals(GlobalVariable.profile.getId());

        setSupportActionBar(toolbar);
        MenuController menuController = new MenuController(QuestionViewActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        replyButton.setText("Add a Reply");
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyButton.setText("Confirm");
                replyInput.setVisibility(View.VISIBLE);
                replyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyInput.setText("");
                        /*
                        String input = replyInput.getText().toString();
                        replyInput.setText("");
                        Reply newReply = new Reply(input);
                        experiment.getQuestions().get(GlobalVariable.indexForQuestionView).addReply(newReply);
                        //TODO: save the experiment
                        */
                    }
                });
            }
        });
        questionContent.setText(experiment.getQuestions().get(GlobalVariable.indexForQuestionView).getContent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        listReplies();
    }

    public void listReplies() {
        replyAdapter = new ReplyAdapter(experiment.getQuestions().get(GlobalVariable.indexForQuestionView).getReplies());
        AdapterController adapterController = new AdapterController(QuestionViewActivity.this, replyListView, replyAdapter);
        adapterController.useAdapter();
    }
}