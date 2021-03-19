package com.example.nerdherd;

import android.app.Activity;
import java.util.ArrayList;

/**
 * Global variables for the app to use when need be
 * @author Zhipeng Z. zhipeng4
 */

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
