package com.example.nerdherd.Database;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.DatabaseListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Use this Database adapter for Testing where we don't want to mess with Firebase.
 */
public class MockDatabaseAdapater extends DatabaseAdapter {

    private ArrayList<ExperimentE> experiments;
    private ArrayList<UserProfile> profiles;
    private HashMap<String, ArrayList<DatabaseListener>> listeners;

    public MockDatabaseAdapater() {
        experiments = new ArrayList<>();
        profiles = new ArrayList<>();
        listeners = new HashMap<>();
    }

    public void generateMockTrials() {

    }

    public void generateMockDatabase() {
        // Experiments
        for(int i=0;i<10;++i) {
            Region r = new Region("Region "+i, new GeoPoint(0, 0), i*50);
            //String experimentId, String description, String ownerId, Region region, int minimumTrials, int type, Timestamp date)
            ExperimentE e = new ExperimentE(String.valueOf(i), "GenericTitle"+i,"Description"+i, String.valueOf(i%10), r, i*2, i%4, Timestamp.now(), false, true);
            experiments.add(e);
        }
    }

    @Override
    public void sendListenerNotification(String tableName, int eventCode, Object data) {

    }

    @Override
    public void addListener(String collectionName, DatabaseListener databaseListener) {
        ArrayList<DatabaseListener> collectionListener = listeners.get(collectionName);
        if(collectionListener == null) {
            collectionListener = new ArrayList<>();
            listeners.put(collectionName, collectionListener);
        }
        collectionListener.add(databaseListener);
    }

    @Override
    public void removeListener(String collectionName, DatabaseListener databaseListener) {
        if(listeners.get(collectionName) == null) {
            return;
        }
        listeners.get(collectionName).remove(databaseListener);
    }

    @Override
    public void saveNewExperiment(ExperimentE experiment) {

    }

    @Override
    public void updateExperiment(ExperimentE experiment) {

    }

    @Override
    public void saveNewProfile(UserProfile profile) {

    }

    @Override
    public void updateProfile(UserProfile profile) {

    }

    @Override
    public void loadExperiments() {

    }

    @Override
    public void loadProfiles() {

    }

    @Override
    public String getNewExperimentId() {
        return null;
    }

    @Override
    public String getNewProfileId() {
        return null;
    }
}
