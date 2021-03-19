package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import android.app.Activity;

import java.util.ArrayList;

public class GlobalVariable {
    public static Profile profile;
    public static ArrayList<Profile> profileArrayList;
    public static ArrayList<Experiment> experimentArrayList;
    public static Activity editProfile;
    public static Integer indexForEdit = -1;
    public static Integer indexForExperimentView = -1;
    public static String experimentType = "No";
    public static Integer experimentMinTrials = -1;
}
