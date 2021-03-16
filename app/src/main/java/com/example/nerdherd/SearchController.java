package com.example.nerdherd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchController {

    public void searchUser(String keyword, ArrayList<Profile> profileList, ArrayList<Profile> resultList, UserNoResultCallBack userNoResultCallBack, UserResultCallBack userResultCallBack, UserNoKeywordCallBack userNoKeywordCallBack){
        if (!keyword.isEmpty()){
            for (Profile userProfile : profileList){
                if (userProfile.getName().toLowerCase().contains(keyword.toLowerCase()) || userProfile.getEmail().toLowerCase().contains(keyword.toLowerCase())){
                    resultList.add(userProfile);
                }
            }
            if (resultList.isEmpty()){
                userNoResultCallBack.onCallback(profileList);
            }
            else{
                userResultCallBack.onCallback(resultList);
            }
        }
        else{
            userNoKeywordCallBack.onCallback(profileList);
        }
    }

    public void searchExperiment (String keyword, ArrayList<Experiment> experimentList, ArrayList<Experiment> resultList,  ExperimentNoResultCallBack experimentNoResultCallBack, ExperimentResultCallBack experimentResultCallBack, ExperimentNoKeywordCallBack experimentNoKeywordCallBack) {
        if (!keyword.isEmpty()) {
            for (Experiment experimentProfile : experimentList) {
                if (experimentProfile.getDescription().toLowerCase().contains(keyword.toLowerCase()) || experimentProfile.getStatus().toLowerCase().contains(keyword.toLowerCase()) || experimentProfile.getTitle().toLowerCase().contains(keyword.toLowerCase()) || experimentProfile.getType().toLowerCase().contains(keyword.toLowerCase()) || experimentProfile.getOwnerProfile().getName().toLowerCase().contains(keyword.toLowerCase()) || experimentProfile.getOwnerProfile().getEmail().toLowerCase().contains(keyword.toLowerCase())) {
                    resultList.add(experimentProfile);
                }
            }
            if (resultList.isEmpty()) {
                experimentNoResultCallBack.onCallback(experimentList);
            }
            else {
                experimentResultCallBack.onCallback(resultList);
            }
        }
        else {
            experimentNoKeywordCallBack.onCallback(experimentList);
        }
    }

    public interface UserNoResultCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }

    public interface UserResultCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }

    public interface UserNoKeywordCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }

    public interface ExperimentNoResultCallBack{
        void onCallback(ArrayList<Experiment> itemList);
    }

    public interface ExperimentResultCallBack{
        void onCallback(ArrayList<Experiment> itemList);
    }

    public interface ExperimentNoKeywordCallBack{
        void onCallback(ArrayList<Experiment> itemList);
    }
}
