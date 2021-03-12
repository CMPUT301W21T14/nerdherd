package com.example.nerdherd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditProfileFragment extends DialogFragment {

    //    private ImageView usersAvatar;
//    private ProfileController profsController;
//    private Intent avatarPick;
//    private Integer avatar;
    private EditText Name;
    private EditText Email;
    private EditText Password;
    //    private Bundle dataBundle;
    private String name;
    private String password;
    private String email;
    private String id;
    private ImageView usersAvatar;
    private Integer avatar;
    private ProfileController profController;
    private FireStoreController fireStoreController;
    private TextView currentname;
    private TextView currentemail;

    private ArrayList<Integer> images;
    private Intent intent;

    public EditProfileFragment(int avatar) {
        this.avatar = avatar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profilefragment, null);

        Name = view.findViewById(R.id.edtusername);
        Email = view.findViewById(R.id.edtuserEmail);
        Password = view.findViewById(R.id.edtuserPassword);
        usersAvatar = view.findViewById(R.id.useravatar);
        ProfileUpdater();
        profController = new ProfileController();
        usersAvatar.setImageResource(profController.getImageArray().get(avatar));

        images = new ProfileController().getImageArray();
        if (GlobalVariable.indexForEdit != -1) {
            usersAvatar.setImageResource(images.get(GlobalVariable.indexForEdit));
        }

        usersAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), AvatarPicker.class);
                intent.putExtra("Profile Edit", true);
                startActivity(intent);
                GlobalVariable.editProfile = (Activity)getContext();
                (GlobalVariable.editProfile).finish();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ProfileActivity) getActivity()).updateUserProfile(Name.getText().toString(), Email.getText().toString(), Password.getText().toString(), GlobalVariable.indexForEdit);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GlobalVariable.indexForEdit = -1;
                    }
                })
                .create();
    }

    public void ProfileUpdater(){
        fireStoreController = new FireStoreController();
        fireStoreController.readProfile(null, GlobalVariable.profile.getId(), "Current User", new FireStoreController.FireStoreProfileCallback() {
            @Override
            public void onCallback(String name, String password, String email, Integer avatar) {
                Name.setText(name+"");
                Email.setText(email+"");
            }
        }, new FireStoreController.FireStoreProfileFailCallback() {
            @Override
            public void onCallback() {
//                Toast.makeText(EditProfileFragment.this, "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        },null, null);
    }
}

