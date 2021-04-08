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

/**
 * Singleton ExperimentManager object
 * All Experiment alterations must be done through this control class to maintain consistency
 * Between database and client
 */
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

    /**
     * Sets the source of data to draw from. Must be compatible with the model
     * @param adapter
     *      DatabaseAdapter - interface allowing for listening in on the datbase for things we care about
     */
    public void setDatabaseAdapter(DatabaseAdapter adapter) {
        databaseAdapter = adapter;
        adapter.addListener("ExperimentE", instance);
    }

    /**
     * ExperimentManager constructore
     *      initialize all the listening vies, and the datasource
     */
    private ExperimentManager() {
        listeningViewsOnChange = new ArrayList<>();
        listeningViewsOnCreate = new ArrayList<>();
        listeningViewsOnLoad = new ArrayList<>();
        experimentList = new ArrayList<>();
    }

    /**
     * Gets the single instance of ExperimentManager to be used within the application
     * @return
     *      ExperimentManager - instance of this object
     */
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

    /**
     * removes an 'OnCreate' experiment listener.
     * @param listener
     */
    public void removeOnCreateListener(ExperimentCreateEventListener listener) {
        listeningViewsOnCreate.remove(listener);
    }

    /**
     * Adds an 'OnDataChanged' listener to experiments. This triggers on any small change
     * @param listener
     *      ExperimentOnChangeEventListener - listener to callback to
     */
    public void addOnChangeListener(ExperimentOnChangeEventListener listener) {
        if(listeningViewsOnChange.add(listener)) {
            return;
        }
        listeningViewsOnChange.add(listener);
    }

    /**
     * Removes the 'OnDataChanged' listener from experiments.
     * @param listener
     *      ExperimentOnChangeEventListener - listener to remove
     */
    public void removeOnChangeListener(ExperimentOnChangeEventListener listener) {
        listeningViewsOnChange.remove(listener);
    }

    /**
     * Adds an 'OnLoadListeer' listener to experiments. This triggers on the initial load of the database
     * @param listener
     *      ExperimentOnChangeEventListener - listener to callback to
     */
    public void addOnLoadListener(ExperimentDataLoadedEventListener listener) {
        if(listeningViewsOnLoad.contains(listener)) {
            return;
        }
        listeningViewsOnLoad.add(listener);
    }

    /**
     * Removes the 'OnLoad' listener from experiments.
     * @param listener
     *      ExperimentOnChangeEventListener - listener to remove
     */
    public void removeOnLoadListener(ExperimentDataLoadedEventListener listener) {
        listeningViewsOnLoad.remove(listener);
    }

    /**
     * Notifies all listeners that 'failedExperiment' was not writte successfully to database.
     * @param failedExperiment
     *      Experiment - object that failed to write
     */
    private void notifyListenerCreateExperimentFailure(ExperimentE failedExperiment) {
        for(ExperimentCreateEventListener listener : listeningViewsOnCreate) {
            if(listener != null) {
                listener.onCreateExperimentFailed(failedExperiment);
            }
        }
    }

    /**
     * Notifies all listeners that 'failedExperiment' wrote successfully to database.
     * @param createdExperiment
     *      Experiment - object that was written to database
     */
    private void notifyListenerCreateExperimentSuccess(ExperimentE createdExperiment) {
        for(ExperimentCreateEventListener listener : listeningViewsOnCreate) {
            if(listener != null) {
                listener.onCreateExperimentSuccess(createdExperiment);
            }
        }
    }

    /**
     * Notifies all listeners that something in the database changed and to verify their data
     */
    private void notifyListenerExperimentDataChanged() {
        for(ExperimentOnChangeEventListener listener : listeningViewsOnChange) {
            if(listener != null) {
                listener.onExperimentDataChanged();
            }
        }
    }

    /**
     * Notifies all listeners when the database initially loads
     */
    private void notifyListenerExperimentsLoaded() {
        for(ExperimentDataLoadedEventListener listener : listeningViewsOnLoad) {
            if(listener != null) {
                listener.onExperimentDataLoaded();
            }
        }
    }

    /********************************************************************************************
     ********* TRIALS
     ********************************************************************************************/

    /**
     * Generates a trial with a location and outcome. Will be associated with proper experiment afterwards
     * @param outcome
     *      float - outcome of the trial
     * @param location
     *      Location - android GPS class, converted to GeoPoint for firebase.
     * @return
     */
    public TrialT generateTrial(float outcome, Location location) {
        GeoPoint geoPoint = null;
        if(location != null) {
            geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        }
        return new TrialT(LocalUser.getUserId(), outcome, geoPoint, Timestamp.now());
    }

    /**
     * Adds a new trial to a given experiment. Will be reflected in the database
     * @param experimentId
     *      String - experimentId of the experiment
     * @param trial
     *      Trial - object of the trial to add to experiment.
     * @return
     *      Returns true if successfully added + attempt to save
     */
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

    /**
     * Creates a new experiment in the database with the following
     * @param title
     *      String - title of experiment
     * @param description
     *      String - description of experiment
     * @param type
     *      int - type of experiment(0=binomial,1=count,2=measurement,3=non-negative)
     * @param minTrials
     *      int - minimum trials required to complete an experiment
     * @param region
     *      Region - Region object with a point location, range, and description
     * @param locationRequired
     *      Boolean - indication whether or not location data must be included with trials
     * @param publish
     *      Boolean - whether or not the experiment is currently published and available for view/participation
     * @param listener
     *      ExperimentCreateEventListener - listener to call back to on successful creation of the experiment.
     */
    public void createExperiment(String title, String description, int type, int minTrials, Region region, boolean locationRequired, boolean publish, ExperimentCreateEventListener listener) {
        // US 01.01.01
        // As an owner, I want to publish an experiment with a description, a region, and a minimum number of trials.
        addOnCreateListener(listener);
        String newId = databaseAdapter.getNewExperimentId();
        ExperimentE experiment = new ExperimentE(newId, title, description, LocalUser.getUserId(), region, minTrials, type, Timestamp.now(), locationRequired, publish);
        experimentList.add(experiment);
        databaseAdapter.saveNewExperiment(experiment);
    }

    /**
     * Subscribe to an experiment
     * @param experimentId
     *      String - experiment Id to subscribe to
     */
    public void subscribeToExperiment(String experimentId) {
        LocalUser.addSubscribedExperiment(experimentId);
    }
    /**
     * Unsubscribe to an experiment
     * @param experimentId
     *      String - experiment Id to unsubscribe to
     */
    public void unsubscribeToExperiment(String experimentId) {
        LocalUser.removeSubscribedExperiment(experimentId);
    }

    /**
     * Publish an experiment for public view
     * @param experimentId
     *      String - experimentId of experiment to publish
     */
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

    /**
     * unpblish an experiment + remove public view
     * @param experimentId
     *      String - experimentId of experiment to unpublish
     */
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

    /**
     * Add a given username to the blacklist for a given experiment. There trials will not be counted
     * @param userName
     *      String - userame to blacklist
     * @param experimentId
     *      String - experimentId to blacklist user from
     * @return
     */
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

    /**
     * Add a question to the associated experimentId
     * @param experimentId
     *      String - experimentId
     * @param question
     *      String - string representation of the question to ask
     */
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

    /**
     * Add a reply to a given question for an experiment
     * @param experimentId
     *      String - experimentId
     * @param questionIdx
     *      int - idnex of the question in the arraylist of given experimentId
     * @param reply
     *      String - string representation of the reply to give to the question
     */
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

    /**
     * End an experiment + allows to show results
     * @param experimentId
     *      String - experimentId
     */
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

    /**
     * Check whether or not an experiment contains a keyword in the description, owner username, or status
     * @param e
     *      Experiment - experiment to check against
     * @param keyword
     *      String - keyword to run against experiment data
     * @return
     *      Boolean - true if contains keyword, false otherwise
     */
    public boolean experimentContainsKeyword(ExperimentE e, String keyword) {
        String ownerUserName = ProfileManager.getProfile(e.getOwnerId()).getUserName();

        if( ownerUserName.toLowerCase().contains(keyword.toLowerCase()) ) {
            return true;
        }

        if( e.getDescription().toLowerCase().contains(keyword.toLowerCase()) ) {
            return true;
        }

        if( e.getStatus().toLowerCase().contains(keyword.toLowerCase()) ) {
            return true;
        }

        if( e.typeToString().toLowerCase().contains(keyword.toLowerCase()) ) {
            return true;
        }

        if( e.getTitle().toLowerCase().contains(keyword.toLowerCase()) ) {
            return true;
        }

        return false;
    }

    /**
     * Returns a list of experiments that contain a keyword
     * @param keyword
     *      String - keyword that must be contained by experiments to make it into the list
     * @return
     *      ArrayList<Experiment> arraylist of experiments containing keyword
     */
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

    /**
     * Small helper function to get total trial count of a given experiment
     * @param experimentId
     *      String - experimentId to get trial count of
     * @return
     */
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

    /**
     * Gets a list of all trials for a given experiment, excluding those added by users who are blacklisted.
     * @param experimentId
     *      String - experimentId to get trial list for
     * @return
     *      ArrayList<Trial> list of trials excluding those made by blacklisted users
     */
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

    /**
     * Helper function, check if ExperimentManager contains the experimentId
     * @param experimentId
     *      String - experimentId to check for
     * @return
     *      Boolean - true if contains, false if not
     */
    private boolean containsExperiment(String experimentId) {
        for( ExperimentE e : experimentList ) {
            if (e.getExperimentId().equals(experimentId) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function to get Experiment object of a given experimentId
     * Can be null, double check on call
     * @param experimentId
     *      String - experimentId to get object of
     * @return
     *      Experiment - experiment object associated with experimentId
     */
    public ExperimentE getExperiment(String experimentId) {
        for( ExperimentE e : experimentList ) {
            if( e.getExperimentId().equals(experimentId) ) {
                return e;
            }
        }
        return null;
    }

    /**
     * Get a list of all experiments the current user owns/created
     * @return
     *      ArrayList<Experiment> - list of experiments owned by user
     */
    public ArrayList<ExperimentE> getOwnedExperiments() {
        ArrayList<ExperimentE> list = new ArrayList<>();
        for(ExperimentE e : experimentList ) {
            if( e.getOwnerId().equals(LocalUser.getUserId()) ) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Called by databaseAdapter callbacks indicating data was changed.
     * @param newData
     *      ArrayList of experiments that contain altered data from what we have
     */
    private void checkExperimentDataChange(ArrayList<ExperimentE> newData) {
        /*
         * Might just be easier to load everything again instead of checking for changes
         */
        experimentList = newData;
        Log.d("Change: ", newData.toString());
        notifyListenerExperimentDataChanged();
    }

    /**
     * Called by databaseAdapter when the database is intially loaded
     * @param data
     *      ArrayList of experiments loaded from database
     */
    public void loadInitialExperimentData(ArrayList<ExperimentE> data) {
        experimentList = data;
        notifyListenerExperimentsLoaded();
    }

    /**
     * Init function of ExperimentFunction to load everything needed
     */
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

    /**
     * Listeners for other objects (mostly views) to implement for calls from ExperimentManager
     */
    public interface ExperimentCreateEventListener {
        void onCreateExperimentFailed(ExperimentE failedToCreate);
        void onCreateExperimentSuccess(ExperimentE createdExperiment);
    }
    /**
     * Listeners for other objects (mostly views) to implement for calls from ExperimentManager
     */
    public interface ExperimentDataLoadedEventListener {
        void onExperimentDataLoaded();
    }
    /**
     * Listeners for other objects (mostly views) to implement for calls from ExperimentManager
     */
    public interface ExperimentOnChangeEventListener {
        void onExperimentDataChanged();
    }
}
