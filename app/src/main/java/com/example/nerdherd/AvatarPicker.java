package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AvatarPicker extends AppCompatActivity {

    private Adapter.onClickListener listener;
    private ProfileController profileController;
    private Intent previousIntent;
    private Intent register;
    private Bundle data;
    private Adapter adapter;
    private Intent intent;
    private AdapterController adapterController;
    private Boolean isEdit;
    private Intent editIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        intent = getIntent();
        isEdit = intent.getBooleanExtra("Profile Edit", false);

        previousIntent = getIntent();
        data = previousIntent.getBundleExtra("Data");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        listener = new Adapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                if (isEdit){
                    editIntent = new Intent(AvatarPicker.this, GlobalVariable.editProfile.getClass());
                    editIntent.putExtra("Index for Edit", index);
                    GlobalVariable.indexForEdit = index;
                    startActivity(editIntent);
                }
                else{
                    register = new Intent(getApplicationContext(), RegisterActivity.class);
                    data.putInt("Index", index);
                    register.putExtra("Data", data);
                    startActivity(register);
                }
                finish();
            }
        };
        profileController = new ProfileController();
        adapter = new Adapter(profileController.getImageArray(), listener);
        adapterController = new AdapterController(AvatarPicker.this, recyclerView, adapter);
        adapterController.useAdapter();
    }
}