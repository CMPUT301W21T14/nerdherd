package com.example.nerdherd.UserInterfaceProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.AvatarListAdapter;

/**
 * Profile avatar picker
 * We have several avatars saved and user is given flexibility to chose the avatars
 * @author Zhipeng Z. zhipeng4
 */
public class AvatarPickerActivity extends AppCompatActivity {

    private AvatarListAdapter.onClickListener listener;
    private AvatarListAdapter adapter;
    private AdapterController adapterController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_picker);

        // Selects the index of the list of avatars, returns it to
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        listener = new AvatarListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int index) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", index);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        };
        adapter = new AvatarListAdapter(LocalUser.imageArray, listener);
        adapterController = new AdapterController(AvatarPickerActivity.this, recyclerView, adapter);
        adapterController.useAdapter();
    }
}