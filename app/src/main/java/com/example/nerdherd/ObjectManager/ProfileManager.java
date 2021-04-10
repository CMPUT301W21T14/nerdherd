package com.example.nerdherd.ObjectManager;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Model.UserProfile;

import java.util.ArrayList;

/**
 * Singleton class that manages all Profiles
 * Potentially overkill as we only deal with our own profile
 * But too late to take out now.
 */
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

    /**
     * Sets listening database adapter to get data from
     * @param adapter
     *      DatabaseAdapter - adapter to get data from
     */
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

    /**
     * notiify listeners of create profile failure
     */
    private void notifyListenerCreateProfileFailure(UserProfile failedProfile) {
        for(ProfileCreateEventListener listener : listeningViewsOnCreate) {
            listener.onCreateProfileFailed(failedProfile);
        }
    }

    /**
     * notiify listeners of profile creation success
     */
    private void notifyListenerCreateProfileSuccess(UserProfile createdProfile) {
        for(ProfileCreateEventListener listener : listeningViewsOnCreate) {
            listener.onCreateProfileSuccess(createdProfile);
        }
    }

    /**
     * notiify listeners of data change
     */
    private void notifyListenerProfileDataChanged() {
        for(ProfileOnChangeEventListener listener : listeningViewsOnChange) {
            listener.onProfileDataChanged();
        }
    }

    /**
     * notiify listeners of initial load
     */
    private void notifyListenerProfilesLoaded() {
        for(ProfileDataLoadedEventListener listener : listeningViewsOnLoad) {
            listener.onProfileDataLoaded();
        }
    }

    /**
     * Init function to load everything
     */
    public void init() {
        databaseAdapter.loadProfiles();
    }

    /**
     * Called when creating a new userProfile
     * @return
     *      String - returns the new UserId generated to store locallyy
     */
    public String createNewProfile() {
        // This check should be done earlier, just in case.
        String newid = databaseAdapter.getNewProfileId();
        UserProfile profile = new UserProfile(newid, newid, "No contact Info available");
        profileList.add(profile);
        databaseAdapter.saveNewProfile(profile);
        return newid;
    }

    /**
     * Edit the contact information of the current user
     * @param newInformation
     *      String - new contact info
     */
    public static void editContactInformation(String newInformation) {
        UserProfile p = getProfile(LocalUser.getUserId());
        p.setContactInfo(newInformation);
        databaseAdapter.updateProfile(p);
    }

    /**
     * Potential future use
     * @param newUsername
     *      String - new username
     */
    public static void setupFirstUsername(String newUsername) {
        UserProfile p = getProfile(LocalUser.getUserId());
        p.setUserName(newUsername);
        databaseAdapter.updateProfile(p);
    }

    /**
     * Fetch the profile by a unique userId
     * @param userId
     *      String - userId of to find
     * @return
     *      UserProfile - profile object associated with userId
     */
    public static UserProfile getProfile(String userId) {
        for( UserProfile p : profileList ) {
            if( p.getUserId().equals(userId) ) {
                return p;
            }
        }
        return null;
    }

    /**
     * Fetch the profile by a unique username instead of userId
     * @param userName
     *      String - username of to find
     * @return
     *      UserProfile - profile object associated with username
     */
    public static UserProfile getProfileByUsername(String userName) {
        for( UserProfile p : profileList ) {
            if( p.getUserName().equals(userName) ) {
                return p;
            }
        }
        return null;
    }

    /**
     * Search for a profile by a keyword; aka search by username
     * @param keyword
     *      String - keyword to search for whitn a users profile
     * @return
     *      ArrayList<UserProfile> list of userprofiles that match critera
     */
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

    /**
     * Check if valid username. Cannot have duplicates!
     * @param userName
     *      String - username that is hopefully unique
     * @return
     *      Boolean - true if username exists, false otherwise
     */
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

    /**
     * Updates a single profile in the database
     * @param userName
     *      String - new userName
     * @param contactInfo
     *      String - new contactInfo
     * @param avatarId
     *      String - index of avatar picture list
     */
    public void updateProfile(String userName, String contactInfo, int avatarId) {
        UserProfile up = getProfile(LocalUser.getUserId());
        up.setContactInfo(contactInfo);
        up.setUserName(userName);
        up.setAvatarId(avatarId);
        databaseAdapter.updateProfile(up);
    }
    /**
     * Triggered by databaseAdapter when db changes
     * @param newData
     *      ArrayList<UserProfile> - list of profiles containing altered data
     */
    private void checkProfileDataChange(ArrayList<UserProfile> newData) {
        /*
         * Might just be easier to load everything again instead of checking for changes
         */
        profileList = newData;
        notifyListenerProfileDataChanged();
    }

    /**
     * Triggered by databaseAdapter when db is initally loaded in
     * @param data
     *      ArrayList<UserProfile> - list of profiles loaded in
     */
    public void loadInitialProfileData(ArrayList<UserProfile> data) {
        profileList = data;
        notifyListenerProfilesLoaded();
    }

    // For testing only
    public ArrayList<UserProfile> getProfileList() {
        return profileList;
    }

    /**
     * Triggered when a database event occurs (Indicating we should alter our data)
     * @param eventCode
     *      int - Database event code (found in DatabaseListener interface)
     * @param data
     *      Object - data associated with event (must Cast to appropriate data type)
     */
    @Override
    public void onDatabaseEvent(int eventCode, Object data) {
        switch(eventCode) {
            case DB_EVENT_PROFILE_CHECK_DATA_CHANGED:
                checkProfileDataChange((ArrayList<UserProfile>) data);
                break;
            case DB_EVENT_PROFILE_LOADED_ON_START:
                loadInitialProfileData((ArrayList<UserProfile>)data);
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

    /**
     * Listen for profile creation events
     */
    public interface ProfileCreateEventListener {
        void onCreateProfileFailed(UserProfile failedToCreate);
        void onCreateProfileSuccess(UserProfile createdProfile);
    }

    /**
     * Listen for initial profile load succes event
     */
    public interface ProfileDataLoadedEventListener {
        void onProfileDataLoaded();
    }

    /**
     * Listen for profile changes in the database
     */
    public interface ProfileOnChangeEventListener {
        void onProfileDataChanged();
    }
}
