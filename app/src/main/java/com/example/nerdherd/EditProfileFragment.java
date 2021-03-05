package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditProfileFragment extends DialogFragment {

    private ImageView usersAvatar;
    private ProfileController profsController;
    private Intent avatarPicker;
    private Integer avatar;
    private EditText Name;
    private EditText Email;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profilefragment, null);

        profsController = new ProfileController();
        usersAvatar = view.findViewById(R.id.useravatar);
        avatar = GlobalVariable.profile.getAvatar();
        usersAvatar.setImageResource(profsController.getImageArray().get(avatar));

        Name = view.findViewById(R.id.edtusername);
        Email = view.findViewById(R.id.edtuserEmail);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ProfileActivity) getActivity()).updateUserInfo(Name.getText().toString(), Email.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}
