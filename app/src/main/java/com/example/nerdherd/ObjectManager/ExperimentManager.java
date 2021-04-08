package com.example.nerdherd.ObjectManager;

import android.location.Location;
import android.os.UserManager;
import android.util.Log;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Experiment;
import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.Question;
import com.example.nerdherd.Reply;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class ExperimentManager implements DatabaseListener {
    private static ExperimentManager instance = null;
    private static DatabaseAdapter databaseAdapter = null;
    private static ArrayList<ExperimentE> experimentList;       // This is your source of truth for experiments - Only ExperimentManager will modify these contents
    private static ArrayList<ExperimentOnChangeEventListener> listeningViewsOnChange;
    private static ArrayList<ExperimentCreateEventListener> listeningViewsOnCreate;
    private static ArrayList<ExperimentDataLoadedEventListener> listeningViewsOnLoad;

    /********************************************************************************************
     ********* INITIALIZATION
     ********************************************************************************************/

    public void setDatabaseAdapter(DatabaseAdapter adapter) {
        databaseAdapter = adapter;
        adapter.addListener("ExperimentE", instance);
    }

    private ExperimentManager() {
        listeningViewsOnChange = new ArrayList<>();
        listeningViewsOnCreate = new ArrayList<>();
        listeningViewsOnLoad = new ArrayList<>();
        experimentList = new ArrayList<>();
    }

    public static ExperimentManager getInstance() {
        if( instance == null ) {
            instance = new ExperimentManager();
        }
        return instance;
    }

    /********************************************************************************************
     ********* LISTENER FUNCTIONS
     ********************************************************************************************/

    /**
     * Add an 'OnCreate' experiment listener. Object will be called when an experiment is created.
     * @param listener
     */
    public void addOnCreateListener(ExperimentCreateEventListener listener) {
        if(listeningViewsOnCreate.contains(listener)) {
            return;
        }
        listeningViewsOnCreate.add(listener);
    }

    public void removeOnCreateListener(ExperimentCreateEventListener listener) {
        listeningViewsOnCreate.remove(listener);
    }

    public void addOnChangeListener(ExperimentOnChangeEventListener listener) {
        if(listeningViewsOnChange.add(listener)) {
            return;
        }
        listeningViewsOnChange.add(listener);
    }

    public void removeOnChangeListener(ExperimentOnChangeEventListener listener) {
        listeningViewsOnChange.remove(listener);
    }

    public void addOnLoadListener(ExperimentDataLoadedEventListener listener) {
        if(listeningViewsOnLoad.contains(listener)) {
            return;
        }
        listeningViewsOnLoad.add(listener);
    }

    public void removeOnLoadListener(ExperimentDataLoadedEventListener listener) {
        listeningViewsOnLoad.remove(listener);
    }

    private void notifyListenerCreateExperimentFailure(ExperimentE failedExperiment) {
        for(ExperimentCreateEventListener listener : listeningViewsOnCreate) {
            if(listener != null) {
                listener.onCreateExperimentFailed(failedExperiment);
            }
        }
    }

    private void notifyListenerCreateExperimentSuccess(ExperimentE createdExperiment) {
        for(ExperimentCreateEventListener listener : listeningViewsOnCreate) {
            if(listener != null) {
                listener.onCreateExperimentSuccess(createdExperiment);
            }
        }
    }

    private void notifyListenerExperimentDataChanged() {
        for(ExperimentOnChangeEventListener listener : listeningViewsOnChange) {
            if(listener != null) {
                listener.onExperimentDataChanged();
            }
        }
    }

    private void notifyListenerExperimentsLoaded() {
        for(ExperimentDataLoadedEventListener listener : listeningViewsOnLoad) {
            if(listener != null) {
                listener.onExperimentDataLoaded();
            }
        }
    }

    /********************************************************************************************
     ********* REGION
     ********************************************************************************************/

    public Region createRegion(String description, GeoPoint location, int range) {
        return new Region(description, location, range);
    }

    /********************************************************************************************
     ********* TRIALS
     ********************************************************************************************/

    public TrialT generateTrial(float outcome, Location location) {
        GeoPoint geoPoint = null;
        if(location != null) {
            geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        }
        return new TrialT(LocalUser.getUserId(), outcome, geoPoint, Timestamp.now());
    }

    public boolean addTrialToExperiment(String experimentId, TrialT trial) {
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            return false;
        }
        e.addTrial(trial);
        databaseAdapter.updateExperiment(e);
        return true;
    }

    /********************************************************************************************
     ********* EXPERIMENT FUNCTIONS
     ********************************************************************************************/

    public void createExperiment(String title, String description, int type, int minTrials, Region region, boolean locationRequired, boolean publish, ExperimentCreateEventListener listener) {
        // US 01.01.01
        // As an owner, I want to publish an experiment with a description, a region, and a minimum number of trials.
        addOnCreateListener(listener);
        String newId = databaseAdapter.getNewExperimentId();
        ExperimentE experiment = new ExperimentE(newId, title, description, LocalUser.getUserId(), region, minTrials, type, Timestamp.now(), locationRequired, publish);
        experimentList.add(experiment);
        databaseAdapter.saveNewExperiment(experiment);
    }

    public void subscribeToExperiment(String experimentId) {
        LocalUser.addSubscribedExperiment(experimentId);
    }

    public void unsubscribeToExperiment(String experimentId) {
        LocalUser.removeSubscribedExperiment(experimentId);
    }

    public void publishExperiment(String experimentId) {
        // US 01.02.01
        // As an owner, I want to unpublish an experiment.
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("publishExp", "Exp=Null");
            return;
        }
        e.setPublished(true);
        databaseAdapter.updateExperiment(e);
    }

    public void unpublishExperiment(String experimentId) {
        // US 01.02.01
        // As an owner, I want to unpublish an experiment.
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("unpublishExp", "Exp=Null");
            return;
        }
        e.setPublished(false);
        databaseAdapter.updateExperiment(e);
    }

    public boolean addUserToExperimentBlacklist(String userName, String experimentId) {
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("ExpBlacklist", "exp=NULL");
            return false;
        }
        UserProfile up = ProfileManager.getProfileByUsername(userName);
        if(up == null) {
            Log.d("ExpBlacklist", "usr=NULL");
            return false;
        }
        e.addToBlacklist(up.getUserId());
        databaseAdapter.updateExperiment(e);
        return true;
    }

    public void addQuestionToExperiment(String experimentId, String question) {
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("addQuestion", "Invalid eId!");
            return;
        }
        Question q = new Question(question);
        e.addQuestion(q);
        databaseAdapter.updateExperiment(e);
    }

    public void addReplyToExperimentQuestion(String experimentId, int questionIdx, String reply) {
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("addReply", "Invalid eId!");
            return;
        }
        if(e.getQuestions() == null) {
            Log.d("addReply", "replyToNull");
            return;
        }
        Question q = e.getQuestions().get(questionIdx);
        if(q == null) {
            Log.d("addReply", "invalidQIdx");
            return;
        }
        q.addReply(new Reply(reply));
        databaseAdapter.updateExperiment(e);
    }

    public void endExperiment(String experimentId) {
        // US 01.03.01
        // As an owner, I want to end an experiment. This leaves the results available and public but does not allow new results to be added.
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            return;
        }
        e.setStatus("Ended");
        databaseAdapter.updateExperiment(e);
    }

    public boolean experimentContainsKeyword(ExperimentE e, String keyword) {
        String ownerUserName = ProfileManager.getProfile(e.getOwnerId()).getUserName();

        if( ownerUserName.contains(keyword) ) {
            return true;
        }

        if( e.getDescription().contains(keyword) ) {
            return true;
        }

        if( e.getStatus().contains(keyword) ) {
            return true;
        }
        return false;
    }

    public ArrayList<ExperimentE> searchForExperimentsByKeyword(String keyword) {
        //  US 05.01.01
        //  As an experimenter, I want to specify a keyword, and search for all experiments that are available.
        //
        //  US 05.02.01
        //  As an experimenter, I want search results to show each experiment with its description, owner username, and status.

        ArrayList<ExperimentE> results = new ArrayList<>();
        for( ExperimentE e : experimentList ) {
            if(experimentContainsKeyword(e, keyword)) {
                results.add(e);
            }
        }

        return results;
    }

    /********************************************************************************************
     ********* HELPER FUCTIONS
     ********************************************************************************************/

    public int getTrialCount(String experimentId) {
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("getTrialCount", "exp=NULL");
            return 0;
        }
        if(e.getTrials() != null) {
            return e.getTrials().size();
        }
        return 0;
    }

    public ArrayList<TrialT> getTrialsExcludeBlacklist(String experimentId) {
        // US 01.08.01
        // As an owner, I want to ignore certain experimenters results.
        ArrayList<TrialT> list = new ArrayList<>();
        ExperimentE e = getExperiment(experimentId);
        if(e == null) {
            Log.d("getTrialBlklst", "exp=NULL");
            return list;
        }
        for( TrialT t : e.getTrials() ) {
            if( e.getUserIdBlacklist().contains(t.getExperimenterId()) ) {
                continue;
            }
            list.add(t);
        }
        return list;
    }

    private boolean containsExperiment(String experimentId) {
        for( ExperimentE e : experimentList ) {
            if (e.getExperimentId().equals(experimentId) ) {
                return true;
            }
        }
        return false;
    }

    public ExperimentE getExperiment(String experimentId) {
        for( ExperimentE e : experimentList ) {
            if( e.getExperimentId().equals(experimentId) ) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<ExperimentE> getOwnedExperiments() {
        ArrayList<ExperimentE> list = new ArrayList<>();
        for(ExperimentE e : experimentList ) {
            if( e.getOwnerId().equals(LocalUser.getUserId()) ) {
                list.add(e);
            }
        }
        return list;
    }

    private void checkExperimentDataChange(ArrayList<ExperimentE> newData) {
        /*
         * Might just be easier to load everything again instead of checking for changes
         */
        experimentList = newData;
        Log.d("Change: ", newData.toString());
        notifyListenerExperimentDataChanged();
    }

    public void loadInitialExperimentData(ArrayList<ExperimentE> data) {
        experimentList = data;
        notifyListenerExperimentsLoaded();
    }

    public void init() {
        databaseAdapter.loadExperiments();
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
        // Some of these may not be needed given firebase is a psychopath and resends the entire database when anything changes
        switch(eventCode) {
            case DB_EVENT_UPDATE_EXPERIMENT_SUCCESS:
                // Unsure if we even need to do anything here
                break;
            case DB_EVENT_UPDATE_EXPERIMENT_FAILURE:
                // Our experiment didn't update ;(
                break;
            case DB_EVENT_EXPERIMENT_CHECK_DATA_CHANGED:
                checkExperimentDataChange((ArrayList<ExperimentE>)data);
                break;
            case DB_EVENT_EXPERIMENT_SAVE_SUCCESS:
                notifyListenerCreateExperimentSuccess((ExperimentE)data);
                break;
            case DB_EVENT_EXPERIMENT_SAVE_FAILURE:
                notifyListenerCreateExperimentSuccess((ExperimentE)data);
                break;
            case DB_EVENT_EXPERIMENT_LOADED_ON_START :
                // Successfully loaded in a new ArrayList<ExperimentE>
                loadInitialExperimentData((ArrayList<ExperimentE>)data);
                break;
            case DB_EVENT_EXPERIMENT_ON_START_LOAD_FAILURE :
                // Initial database load event fails

                break;
            default:
                break;
        }
    }

    public interface ExperimentCreateEventListener {
        void onCreateExperimentFailed(ExperimentE failedToCreate);
        void onCreateExperimentSuccess(ExperimentE createdExperiment);
    }
    public interface ExperimentDataLoadedEventListener {
        void onExperimentDataLoaded();
    }

    public interface ExperimentOnChangeEventListener {
        void onExperimentDataChanged();
    }
}
