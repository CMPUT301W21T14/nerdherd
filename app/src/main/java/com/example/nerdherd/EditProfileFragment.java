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

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Edit profile fragment class
 * Fragment class for the user/owner to edit profile once the profile has been created
 * @author Zhipeng Z. zhipeng4
 */

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

    private ProfileController profController;
    private FireStoreController fireStoreController;
    private TextView currentname;
    private TextView currentemail;

    private ProfileManager pMgr = ProfileManager.getInstance();

    private Intent intent;
    private int newAvatar;

    public static int REQUEST_CODE_AVATAR_SELECTOR = 1;

    public EditProfileFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profilefragment, null);

        Name = view.findViewById(R.id.et_username);
        Email = view.findViewById(R.id.et_contact_info);
        Password = view.findViewById(R.id.edit_password);
        usersAvatar = view.findViewById(R.id.useravatar);
        ProfileManager pMgr = ProfileManager.getInstance();
        UserProfile up = pMgr.getProfile(LocalUser.getUserId());

        usersAvatar.setImageResource(LocalUser.imageArray.get(up.getAvatarId()));
        newAvatar = up.getAvatarId();

        Name.setText(up.getUserName());
        Email.setText(up.getContactInfo());

        usersAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), AvatarPicker.class);
                startActivityForResult(intent, REQUEST_CODE_AVATAR_SELECTOR);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Name = view.findViewById(R.id.et_username);
                        Email = view.findViewById(R.id.et_contact_info);
                        saveProfile(Name.getText().toString(), Email.getText().toString(), newAvatar);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .create();
    }

    private void saveProfile(String newUsername, String newContactInfo, int newAvatar) {
        pMgr.updateProfile(newUsername, newContactInfo, newAvatar);
    }

    /*public void ProfileUpdater(){
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
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_AVATAR_SELECTOR) {
            if(resultCode == Activity.RESULT_OK) {
                newAvatar = data.getIntExtra("result", 0);
                usersAvatar.setImageResource(LocalUser.imageArray.get(newAvatar));
                Log.d("new profile", String.valueOf(newAvatar));
            }
        }
    }
}

