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
                    }
                });
            }
        });

        questionContent.setText(experiment.getQuestions().get(GlobalVariable.indexForQuestionView).getContent());
        listReplies(experiment.getQuestions().get(GlobalVariable.indexForQuestionView).getReplies());
    }

    public void listReplies(ArrayList<Reply> replies) {
        GlobalVariable.replyArrayList = replies;
        ReplyAdapter replyAdapter = new ReplyAdapter(replies);
        AdapterController adapterController = new AdapterController(QuestionViewActivity.this, replyListView, replyAdapter);
        adapterController.useAdapter();
    }
}