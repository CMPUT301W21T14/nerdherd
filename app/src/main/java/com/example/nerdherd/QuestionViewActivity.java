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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

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
                        String input = replyInput.getText().toString();
                        replyInput.setText("");
                        Reply newReply = new Reply(input);
                        experiment.getQuestions().get(GlobalVariable.indexForQuestionView).addReply(newReply);
                        saveExperimentReplies();
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

    public void saveExperimentReplies() {
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
                listReplies();
            }
        }, new FireStoreController.FireStoreUpdateFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listReplies() {
        replyAdapter = new ReplyAdapter(experiment.getQuestions().get(GlobalVariable.indexForQuestionView).getReplies());
        AdapterController adapterController = new AdapterController(QuestionViewActivity.this, replyListView, replyAdapter);
        adapterController.useAdapter();
    }
}