package com.example.nerdherd.Database;


import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.DatabaseListener;

public abstract class DatabaseAdapter {

    public abstract void sendListenerNotification(String tableName, int eventCode, Object data);

    public abstract void addListener(String tableName, DatabaseListener listener);
    public abstract void removeListener(String tableName, DatabaseListener listener);

    public abstract void saveNewExperiment(Experiment experiment);
    public abstract void updateExperiment(Experiment experiment);
    public abstract void saveNewProfile(UserProfile profile);
    public abstract void updateProfile(UserProfile profile);

    public abstract void loadExperiments();
    public abstract void loadProfiles();

    public abstract String getNewExperimentId();
    public abstract String getNewProfileId();
}
