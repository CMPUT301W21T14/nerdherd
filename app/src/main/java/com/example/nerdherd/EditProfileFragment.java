package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ProfileActivity) getActivity()).updateUserProfile(Name.getText().toString(), Email.getText().toString(), Password.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
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

