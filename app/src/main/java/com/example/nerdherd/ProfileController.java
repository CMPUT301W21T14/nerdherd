package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class ProfileController {
    private String name;
    private String password;
    private String email;
    private String id;
    private Integer avatar;
    private Profile profile;
    private Integer[] imageList= {R.drawable.zelda, R.drawable.f1, R.drawable.f2, R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5, R.drawable.h};
    private ArrayList<Integer> imageArray = new ArrayList(Arrays.asList(imageList));

    // Constructor
    public ProfileController(String name, String password, String email, String id, Integer avatar) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.id = id;
        this.avatar = avatar;
    }

    public ProfileController() {
        this.name = "name";
        this.password = "password";
        this.email = "email";
        this.id = "id";
        this.avatar = 0;
    }

    public ArrayList<Integer> getImageArray() {
        return imageArray;
    }

    public void creator(){
        profile = new Profile(name, password, email, avatar);
    }

    public Profile getProfile() {
        return profile;
    }
}
