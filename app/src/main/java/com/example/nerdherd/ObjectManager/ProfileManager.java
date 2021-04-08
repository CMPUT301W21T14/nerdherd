package com.example.nerdherd.ObjectManager;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.UserProfile;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ProfileManager implements DatabaseListener {

    private static ProfileManager instance = null;
    private static DatabaseAdapter databaseAdapter = null;

    private static ArrayList<UserProfile> profileList;       // This is your source of truth for profiles - Only ProfileManager will modify these contents
    private static ArrayList<ProfileCreateEventListener> listeningViewsOnCreate;
    private static ArrayList<ProfileDataLoadedEventListener> listeningViewsOnLoad;
    private static ArrayList<ProfileOnChangeEventListener> listeningViewsOnChange;

    // US 04.01.01
    // As an owner or experimenter, I want a profile with a unique username and my contact information.
    //
    // US 04.02.01
    // As an owner or experimenter, I want to edit the contact information in my profile.
    //
    // US 04.03.01
    // As an owner or experimenter, I want to retrieve and show the profile of a presented username.
    //
    // US 04.04.01 *new for Part 4*
    //
    // As an owner or experimenter, I do not want to log into my application using a username and password.

    private ProfileManager() {
        listeningViewsOnCreate = new ArrayList<>();
        listeningViewsOnLoad = new ArrayList<>();
        listeningViewsOnChange = new ArrayList<>();
        profileList = new ArrayList<>();
    }

    public static ProfileManager getInstance() {
        if( instance == null ) {
            instance = new ProfileManager();
        }
        return instance;
    }

    public void setDatabaseAdapter(DatabaseAdapter adapter) {
        databaseAdapter = adapter;
        adapter.addListener("UserProfile", instance);
    }

    /********************************************************************************************
     ********* LISTENER FUNCTIONS
     ********************************************************************************************/

    public void addOnChangeListener(ProfileOnChangeEventListener listener) {
        if(listeningViewsOnChange.contains(listener)) {
            return;
        }
        listeningViewsOnChange.add(listener);
    }

    public void removeOnChangeListener(ProfileOnChangeEventListener listener) {
        listeningViewsOnChange.remove(listener);
    }

    public void addOnLoadListener(ProfileDataLoadedEventListener listener) {
        if(listeningViewsOnLoad.contains(listener)) {
            return;
        }
        listeningViewsOnLoad.add(listener);
    }

    public void removeOnLoadListener(ProfileDataLoadedEventListener listener) {
        listeningViewsOnLoad.remove(listener);
    }

    public void addOnCreateListener(ProfileCreateEventListener listener) {
        if(listeningViewsOnCreate.contains(listener)) {
            return;
        }
        listeningViewsOnCreate.add(listener);
    }

    public void removeOnCreateListener(ProfileCreateEventListener listener) {
        listeningViewsOnCreate.remove(listener);
    }

    private void notifyListenerCreateProfileFailure(UserProfile failedProfile) {
        for(ProfileCreateEventListener listener : listeningViewsOnCreate) {
            listener.onCreateProfileFailed(failedProfile);
        }
    }

    private void notifyListenerCreateProfileSuccess(UserProfile createdProfile) {
        for(ProfileCreateEventListener listener : listeningViewsOnCreate) {
            listener.onCreateProfileSuccess(createdProfile);
        }
    }

    private void notifyListenerProfileDataChanged() {
        for(ProfileOnChangeEventListener listener : listeningViewsOnChange) {
            listener.onProfileDataChanged();
        }
    }

    private void notifyListenerProfilesLoaded() {
        for(ProfileDataLoadedEventListener listener : listeningViewsOnLoad) {
            listener.onProfileDataLoaded();
        }
    }

    public void init() {
        databaseAdapter.loadProfiles();
    }

    public String createNewProfile() {
        // This check should be done earlier, just in case.
        String newid = databaseAdapter.getNewProfileId();
        UserProfile profile = new UserProfile(newid, newid, "No contact Info available");
        profileList.add(profile);
        databaseAdapter.saveNewProfile(profile);
        return newid;
    }

    public static void editContactInformation(String newInformation) {
        UserProfile p = getProfile(LocalUser.getUserId());
        p.setContactInfo(newInformation);
        databaseAdapter.updateProfile(p);
    }

    public static void setupFirstUsername(String newUsername) {
        UserProfile p = getProfile(LocalUser.getUserId());
        p.setUserName(newUsername);
        databaseAdapter.updateProfile(p);
    }

    public static UserProfile getProfile(String userId) {
        for( UserProfile p : profileList ) {
            if( p.getUserId().equals(userId) ) {
                return p;
            }
        }
        return null;
    }

    public static UserProfile getProfileByUsername(String userName) {
        for( UserProfile p : profileList ) {
            if( p.getUserName().equals(userName) ) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<UserProfile> searchProfileByKeyword(String keyword) {
        ArrayList<UserProfile> list = new ArrayList<>();
        for( UserProfile p : profileList ) {
            if(p.getContactInfo().contains(keyword)) {
                list.add(p);
                continue;
            }
            if(p.getUserName().contains(keyword)) {
                list.add(p);
                continue;
            }
            if(p.getUserId().contains(keyword)) {
                list.add(p);
                continue;
            }
        }
        return list;
    }

    public boolean isValidUsername(String userName) {
        // Check if duplicate usernames
        for( UserProfile p : profileList ) {
            if( p.getUserName().equals(userName) ) {
                return false;
            }
        }

        // Can also check for length ect

        return true;
    }

    public void updateProfile(String userName, String contactInfo, int avatarId) {
        UserProfile up = getProfile(LocalUser.getUserId());
        up.setContactInfo(contactInfo);
        up.setUserName(userName);
        up.setAvatarId(avatarId);
        databaseAdapter.updateProfile(up);
    }

    private void checkProfileDataChange(ArrayList<UserProfile> newData) {
        /*
         * Might just be easier to load everything again instead of checking for changes
         */
        profileList = newData;
        notifyListenerProfileDataChanged();
    }

    public void loadInitialExperimentData(ArrayList<UserProfile> data) {
        profileList = data;
        notifyListenerProfilesLoaded();
    }

    @Override
    public void onDatabaseEvent(int eventCode, Object data) {
        switch(eventCode) {
            case DB_EVENT_PROFILE_CHECK_DATA_CHANGED:
                checkProfileDataChange((ArrayList<UserProfile>) data);
                break;
            case DB_EVENT_PROFILE_LOADED_ON_START:
                loadInitialExperimentData((ArrayList<UserProfile>)data);
                break;
            case DB_EVENT_PROFILE_ON_START_LOAD_FAILURE:
                break;
            case DB_EVENT_PROFILE_SAVE_NEW_SUCCESS:
                notifyListenerCreateProfileSuccess((UserProfile)data);
                break;
            case DB_EVENT_PROFILE_SAVE_NEW_FAILURE:
                notifyListenerCreateProfileFailure((UserProfile)data);
                break;
            case DB_EVENT_UPDATE_PROFILE_SUCCESS:
                notifyListenerProfileDataChanged();
                break;
            case DB_EVENT_UPDATE_PROFILE_FAILURE:
                break;
        }
    }

    public interface ProfileCreateEventListener {
        void onCreateProfileFailed(UserProfile failedToCreate);
        void onCreateProfileSuccess(UserProfile createdProfile);
    }
    public interface ProfileDataLoadedEventListener {
        void onProfileDataLoaded();
    }

    public interface ProfileOnChangeEventListener {
        void onProfileDataChanged();
    }
}
