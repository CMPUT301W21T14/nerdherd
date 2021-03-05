package com.example.nerdherd;

import android.app.AlertDialog;
import android.content.DialogInterface;

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

    public interface UserNoResultCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }

    public interface UserResultCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }

    public interface UserNoKeywordCallBack{
        void onCallback(ArrayList<Profile> itemList);
    }
}
