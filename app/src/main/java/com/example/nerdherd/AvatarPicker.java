package com.example.nerdherd;

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
    private Intent priviousIntent;
    private Intent register;
    private Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        priviousIntent = getIntent();
        data = priviousIntent.getBundleExtra("Data");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        listener = new Adapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                register = new Intent(getApplicationContext(), RegisterActivity.class);
                data.putInt("Index", index);
                register.putExtra("Data", data);
                startActivity(register);
                finish();
            }
        };
        profileController = new ProfileController();
        Adapter adapter = new Adapter(profileController.getImageArray(), listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}