package com.example.nerdherd.Deprecated;

import com.example.nerdherd.Deprecated.Experiment_Deprecated;
import com.example.nerdherd.Deprecated.Profile;

import java.util.ArrayList;

/**
 * Search in the app is controlled by this class
 * The controller class for Adapter to get notified of the user's behaviour and update the model class as needed
 * @author Utkarsh S. usaraswa
 * @author Zhipeng Z. zhipeng4
 * @author Harjot S. harjotsi
 */

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

    public void searchExperiment (String keyword, ArrayList<Experiment_Deprecated> experimentDeprecatedList, ArrayList<Experiment_Deprecated> resultList, ExperimentNoResultCallBack experimentNoResultCallBack, ExperimentResultCallBack experimentResultCallBack, ExperimentNoKeywordCallBack experimentNoKeywordCallBack) {
        if (!keyword.isEmpty()) {
            for (Experiment_Deprecated experimentDeprecatedProfile : experimentDeprecatedList) {
                if (experimentDeprecatedProfile.getDescription().toLowerCase().contains(keyword.toLowerCase()) || experimentDeprecatedProfile.getStatus().toLowerCase().contains(keyword.toLowerCase()) || experimentDeprecatedProfile.getTitle().toLowerCase().contains(keyword.toLowerCase()) || experimentDeprecatedProfile.getType().toLowerCase().contains(keyword.toLowerCase()) || experimentDeprecatedProfile.getOwnerProfile().getName().toLowerCase().contains(keyword.toLowerCase()) || experimentDeprecatedProfile.getOwnerProfile().getEmail().toLowerCase().contains(keyword.toLowerCase())) {
                    resultList.add(experimentDeprecatedProfile);
                }
            }
            if (resultList.isEmpty()) {
                experimentNoResultCallBack.onCallback(experimentDeprecatedList);
            }
            else {
                experimentResultCallBack.onCallback(resultList);
            }
        }
        else {
            experimentNoKeywordCallBack.onCallback(experimentDeprecatedList);
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
        void onCallback(ArrayList<Experiment_Deprecated> itemList);
    }

    public interface ExperimentResultCallBack{
        void onCallback(ArrayList<Experiment_Deprecated> itemList);
    }

    public interface ExperimentNoKeywordCallBack{
        void onCallback(ArrayList<Experiment_Deprecated> itemList);
    }
}
