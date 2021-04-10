package com.example.nerdherd.Database;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.DatabaseListener;
import com.example.nerdherd.Model.Question;
import com.example.nerdherd.Model.Reply;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Use this Database adapter for Testing where we don't want to mess with Firebase.
 */
public class MockDatabaseAdapater extends DatabaseAdapter {

    public final static String COLLECTION_EXPERIMENT = "ExperimentE";
    public final static String COLLECTION_PROFILE = "UserProfile";

    public final static String MOCK_EXISTING_EXPERIMENT_ID = "0";
    public final static String MOCK_NON_EXISTANT_EXPERIMENT_ID = "15";

    private int nextIdProfile;
    private int nextIdExperiment;

    private ListenerRegistration profileSnapshotListener = null;
    private ListenerRegistration experimentSnapshotListener = null;

    private ArrayList<Experiment> experiments;
    private ArrayList<UserProfile> profiles;
    private HashMap<String, ArrayList<DatabaseListener>> listeners;

    public MockDatabaseAdapater() {
        experiments = new ArrayList<>();
        profiles = new ArrayList<>();
        listeners = new HashMap<>();
    }

    /**
     * Generate 10 mock Experiments with 3 Questions, each with one reply
     */
    public void generateMockExperiments() {
        for(int i=0;i<10;++i) {
            Region r = new Region("Region "+i, new GeoPoint(i*2, i), i*50);
            //String experimentId, String description, String ownerId, Region region, int minimumTrials, int type, Timestamp date)
            Experiment e = new Experiment(String.valueOf(i), "GenericTitle"+i,"Description"+i, String.valueOf(i%5), r, i*2, i%4, Timestamp.now(), false, true);
            for(int j=0;j<3;++j) {
                Question q = new Question("QuestionExp" + i + "Question"+j);
                q.addReply(new Reply("Single Reply"));
                e.addQuestion(q);
            }
            experiments.add(e);
        }
        nextIdExperiment = 10;
    }

    /**
     * Generate mock profiles, Id's 0-5
     */
    public void generateMockProfiles() {
        for(int i=0;i<5;++i) {
            UserProfile profile = new UserProfile(String.valueOf(i), "Username"+i, "ContactInfo"+i);
            profiles.add(profile);
        }
        nextIdProfile=5;
    }

    /**
     * Init all the database needed
     */
    public void init() {
        loadExperiments();
        loadProfiles();
    }

    /**
     * Add a class which will listen for database changes on a certain collection
     * @param collectionName
     *      String - name of the collection
     * @param databaseListener
     *      DatabaseListener - object to give callbacks to
     */
    public void addListener(String collectionName, DatabaseListener databaseListener) {

        ArrayList<DatabaseListener> collectionListener = listeners.get(collectionName);
        if(collectionListener == null) {
            collectionListener = new ArrayList<>();
            listeners.put(collectionName, collectionListener);
        }

        collectionListener.add(databaseListener);
    }

    /**
     * Remove a class which will listen for database changes on a certain table/collection
     * @param collectionName
     *      String - name of the collection
     * @param databaseListener
     *      DatabaseListener - object to remove
     */
    public void removeListener(String collectionName, DatabaseListener databaseListener) {
        if(listeners.get(collectionName) == null) {
            return;
        }
        listeners.get(collectionName).remove(databaseListener);
    }

    /**
     * notify Listeners of a specific database collection/table about certain events as indicated by the eventCode
     * @param collectionName
     *      String - name of the collection/table associated with the event
     * @param eventCode
     *      int - code associated with the event that happened
     * @param data
     *      data - any relevant data to update the listener with
     */
    public void sendListenerNotification(String collectionName, int eventCode, Object data) {
        if( listeners.get(collectionName) == null ) {
            return;
        }
        for( DatabaseListener listener : listeners.get(collectionName) ) {
            listener.onDatabaseEvent(eventCode, data);
        }
    }

    /**
     * Stops listening to profile database (array)
     * Never used in mock database but keep in case
     */
    public void stopListeningForProfileChanges() {
        if (profileSnapshotListener != null) {
            profileSnapshotListener.remove();
        }
    }

    /**
     * Stops listening to experiment database (array)
     * Never used in mock database, but will keep in case
     */
    public void stopListeningForExperimentChanges() {
        if (experimentSnapshotListener != null) {
            experimentSnapshotListener.remove();
        }
    }

    /**
     * Helper function for mock database
     * @param userId
     * @return
     */
    public UserProfile containsProfile(String userId) {
        for( UserProfile e : profiles ) {
            if(e.getUserId().equals(userId)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Helper function for mock database
     * @param experimentId
     * @return
     */
    public Experiment containsExperiment(String experimentId) {
        for( Experiment e : experiments ) {
            if(e.getExperimentId().equals(experimentId)) {
                return e;
            }
        }
        return null;
    }
    /**
     * Save a brand new experiment to database
     * @param experiment
     */
    @Override
    public void saveNewExperiment(Experiment experiment) {
        Experiment e = containsExperiment(experiment.getExperimentId());
        if(e == null) {
            experiments.add(experiment);
            sendListenerNotification(COLLECTION_EXPERIMENT,  DatabaseListener.DB_EVENT_EXPERIMENT_SAVE_SUCCESS, experiment);
            sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_CHECK_DATA_CHANGED, experiments);
        } else {
            sendListenerNotification(COLLECTION_EXPERIMENT,  DatabaseListener.DB_EVENT_EXPERIMENT_SAVE_FAILURE, experiment);
        }
    }

    /**
     * Save a brand new profile in the mock database
     * @param profile
     *      Profile - profile object
     */
    @Override
    public void saveNewProfile(UserProfile profile) {
        UserProfile e = containsProfile(profile.getUserId());
        if(e == null) {
            profiles.add(profile);
            sendListenerNotification(COLLECTION_PROFILE,  DatabaseListener.DB_EVENT_PROFILE_SAVE_NEW_SUCCESS, profile);
            sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_CHECK_DATA_CHANGED, profiles);
        } else {
            sendListenerNotification(COLLECTION_PROFILE,  DatabaseListener.DB_EVENT_PROFILE_SAVE_NEW_FAILURE, profile);
        }
    }

    private void removeOldExperiment(String experimentId) {
        for ( Experiment e : experiments ) {
            if(e.getExperimentId().equals(experimentId)) {
                experiments.remove(e);
                return;
            }
        }
    }

    /**
     * Update a single experiment in the mock database.
     * @param experiment
     */
    @Override
    public void updateExperiment(Experiment experiment) {
        Experiment e = containsExperiment(experiment.getExperimentId());
        if(e != null) {
            removeOldExperiment(experiment.getExperimentId());
            experiments.add(experiment);
            sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_SUCCESS, experiment);
            sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_CHECK_DATA_CHANGED, experiments);
        } else {
            sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_FAILURE, experiment);
        }
    }


    private void removeOldProfile(String userId) {
        for ( UserProfile e : profiles ) {
            if(e.getUserId().equals(userId)) {
                profiles.remove(e);
                return;
            }
        }
    }


    /**
     * updates single profile
     * @param profile
     */
    @Override
    public void updateProfile(UserProfile profile) {
        UserProfile e = containsProfile(profile.getUserId());
        if(e != null) {
            removeOldProfile(profile.getUserId());
            profiles.add(profile);
            sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_UPDATE_PROFILE_SUCCESS, profile);
            sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_CHECK_DATA_CHANGED, profiles);
        } else {
            sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_UPDATE_PROFILE_FAILURE, profile);
        }
    }

    /**
     * Load all experiments in the database (At startup)
     */
    @Override
    public void loadExperiments() {
        generateMockExperiments();
        sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_LOADED_ON_START, experiments);
    }

    /**
     * Load all profiles in the database (At startup)
     */
    @Override
    public void loadProfiles() {
        generateMockProfiles();
        sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_LOADED_ON_START, profiles);
    }

    /**
     * Generate a new experiment Id from our mock database
     * @return
     *      String - the Id which can be used for a new experiment
     */
    @Override
    public String getNewExperimentId() {
        nextIdExperiment+=1;
        return String.valueOf(nextIdExperiment-1);
    }

    /**
     * Generate a new profile Id from mock database
     * @return
     *      String - the Id which can be used for a new profile
     */
    @Override
    public String getNewProfileId() {
        nextIdProfile+=1;
        return String.valueOf(nextIdProfile-1);
    }
}
