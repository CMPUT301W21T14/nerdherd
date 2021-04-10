package com.example.nerdherd.UserInterfaceProfile;

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
import android.widget.Toast;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;
import com.example.nerdherd.UserInterfaceProfile.AvatarPickerActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Edit profile fragment class
 * Fragment class for the user/owner to edit profile once the profile has been created
 * @author Zhipeng Z. zhipeng4
 */
public class EditProfileFragment extends DialogFragment {

    private EditText nameEditText;
    private EditText emailEditText;

    private ImageView usersAvatar;

    private ProfileManager pMgr = ProfileManager.getInstance();

    private Intent intent;
    private int newAvatar;

    public static int REQUEST_CODE_AVATAR_SELECTOR = 1;

    public EditProfileFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment, null);

        nameEditText = view.findViewById(R.id.et_username);
        emailEditText = view.findViewById(R.id.et_contact_info);
        usersAvatar = view.findViewById(R.id.useravatar);
        ProfileManager pMgr = ProfileManager.getInstance();
        UserProfile up = pMgr.getProfile(LocalUser.getUserId());

        usersAvatar.setImageResource(LocalUser.imageArray.get(up.getAvatarId()));
        newAvatar = up.getAvatarId();

        nameEditText.setText(up.getUserName());
        emailEditText.setText(up.getContactInfo());

        usersAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), AvatarPickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_AVATAR_SELECTOR);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nameEditText = view.findViewById(R.id.et_username);
                        emailEditText = view.findViewById(R.id.et_contact_info);
                        saveProfile(nameEditText.getText().toString(), emailEditText.getText().toString(), newAvatar);
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

    // The only profile we can update is our own
    private void saveProfile(String newUsername, String newContactInfo, int newAvatar) {
        if(pMgr.getProfileByUsername(newUsername) != null) {
            // Couldn't get it to work
            //Toast.makeText(getActivity(), "Username taken!", Toast.LENGTH_LONG);
            return;
        }
        pMgr.updateProfile(newUsername, newContactInfo, newAvatar);
    }

    /**
     * When the avatar selector returns with the selected avatar we need to update the views
     */
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

