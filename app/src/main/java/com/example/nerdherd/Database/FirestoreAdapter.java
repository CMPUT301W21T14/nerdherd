package com.example.nerdherd.Database;

import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.DatabaseListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class interacts with firebase and acts as an intermediary between the database
 * and any ObjectManagers that listen to it for updates.
 */
public class FirestoreAdapter extends DatabaseAdapter {
    public final static String COLLECTION_EXPERIMENT = "ExperimentE";
    public final static String COLLECTION_PROFILE = "UserProfile";

    private ListenerRegistration profileSnapshotListener = null;
    private ListenerRegistration experimentSnapshotListener = null;

    private FirebaseFirestore dbInstance = null;

    protected HashMap<String, ArrayList<DatabaseListener>> listeners;

    public FirestoreAdapter() {
        listeners = new HashMap<>();
        dbInstance = FirebaseFirestore.getInstance();
    }

    /**
     * Initialize all databases required for the adapter. Must be done manually
     */
    public void init() {
        loadExperiments();
        loadProfiles();
    }

    /**
     * Add a class which implements DatabaseListener to listen for database changes on a certain collection
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

        switch (collectionName) {
            case COLLECTION_EXPERIMENT:
                if (experimentSnapshotListener == null) {
                    startListeningForExperimentChanges();
                }
                break;
            case COLLECTION_PROFILE:
                if (profileSnapshotListener == null) {
                    startListeningForProfileChanges();
                }
                break;
            default:
                break;
        }

    }

    /**
     * Remove a class which will listen for database changes on a certain collection
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
     * Begin listening to Firebase for changes to Experiment Collections
     */
    public void startListeningForExperimentChanges() {
        CollectionReference ref = dbInstance.collection(COLLECTION_EXPERIMENT);
        experimentSnapshotListener = ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Experiment> experiments = new ArrayList<>();
                for( QueryDocumentSnapshot doc : value ) {
                    Experiment e = processExperimentSnapshot(doc);
                    experiments.add(e);
                }
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_CHECK_DATA_CHANGED, experiments);
            }
        });
    }


    /**
     * Begin listening to Profile collection for changes
     */
    public void startListeningForProfileChanges() {
        CollectionReference ref = dbInstance.collection(COLLECTION_PROFILE);
        profileSnapshotListener = ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<UserProfile> profiles = new ArrayList<>();
                for( QueryDocumentSnapshot doc : value ) {
                    UserProfile p = processProfileSnapshot(doc);
                    profiles.add(p);
                }
                sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_CHECK_DATA_CHANGED, profiles);
            }
        });
    }

    /**
     * Stop listening to profile collection for changes
     */
    public void stopListeningForProfileChanges() {
        if (profileSnapshotListener != null) {
            profileSnapshotListener.remove();
        }
    }

    /**
     * Stop listening to experiment collection for changes
     */
    public void stopListeningForExperimentChanges() {
        if (experimentSnapshotListener != null) {
            experimentSnapshotListener.remove();
        }
    }

    /**
     * Function to process the breakdown of a document from Experiment database
     * @param doc
     *      QueryDocumentSnapshot - potentially modified document
     */
    public Experiment processExperimentSnapshot(QueryDocumentSnapshot doc) {
        Experiment experiment = doc.toObject(Experiment.class);
        return experiment;
    }

    /**
     * Function to process the breakdown of a document from Profile database
     * @param doc
     */
    public UserProfile processProfileSnapshot(QueryDocumentSnapshot doc) {
        UserProfile profile = doc.toObject(UserProfile.class);
        return profile;
    }

    /**
     * Save a brand new experiment to database
     * @param experiment
     */
    @Override
    public void saveNewExperiment(Experiment experiment) {
        CollectionReference ref = dbInstance.collection(COLLECTION_EXPERIMENT);
        ref.document(experiment.getExperimentId()).set(experiment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendListenerNotification(COLLECTION_EXPERIMENT,  DatabaseListener.DB_EVENT_EXPERIMENT_SAVE_SUCCESS, experiment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_EXPERIMENT,  DatabaseListener.DB_EVENT_EXPERIMENT_SAVE_FAILURE, experiment);
            }
        });
    }

    /**
     * Save a brand new profile in the database
     * @param profile
     *      Profile - profile object
     */
    @Override
    public void saveNewProfile(UserProfile profile) {
        CollectionReference ref = dbInstance.collection(COLLECTION_PROFILE);
        ref.document(profile.getUserId()).set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendListenerNotification(COLLECTION_PROFILE,  DatabaseListener.DB_EVENT_PROFILE_SAVE_NEW_SUCCESS, profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_PROFILE,  DatabaseListener.DB_EVENT_PROFILE_SAVE_NEW_FAILURE, profile);
            }
        });
    }

    /**
     * Update a single experiment in the database.
     * Triggers DB_EVENT_UPDATE_EXPERIMENT_SUCCESS and DB_EVENT_UPDATE_EXPERIMENT_FAILURE
     * @param experiment
     * @see Experiment - altered experiment to overwrite in the database
     */
    @Override
    public void updateExperiment(Experiment experiment) {
        CollectionReference ref = dbInstance.collection(COLLECTION_EXPERIMENT);
        ref.document(experiment.getExperimentId()).set(experiment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_SUCCESS, experiment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_FAILURE, experiment);
            }
        });
    }

    /**
     * Update a single profile in the database
     * Triggers DB_EVENT_UPDATE_EXPERIMENT_SUCCESS and DB_EVENT_UPDATE_EXPERIMENT_FAILURE
     * @param profile
     *      @see UserProfile - adds the (hopefully edited) profile to the database, which will overwrite old data
     */
    @Override
    public void updateProfile(UserProfile profile) {
        CollectionReference ref = dbInstance.collection(COLLECTION_PROFILE);
        ref.document(profile.getUserId()).set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_SUCCESS, profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_UPDATE_EXPERIMENT_FAILURE, profile);
            }
        });
    }

    /**
     * Load all experiments in the database (At startup)
     * Triggers DB_EVENT_EXPERIMENT_LOADED_ON_START and DB_EVENT_EXPERIMENT_ON_START_LOAD_FAILURE
     */
    @Override
    public void loadExperiments() {
        CollectionReference ref = dbInstance.collection(COLLECTION_EXPERIMENT);
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Experiment> experiments = new ArrayList<>();
                for( QueryDocumentSnapshot doc : queryDocumentSnapshots ) {
                    Experiment e = processExperimentSnapshot(doc);
                    if(e != null) {
                        experiments.add(e);
                    }
                }
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_LOADED_ON_START, experiments);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_EXPERIMENT, DatabaseListener.DB_EVENT_EXPERIMENT_ON_START_LOAD_FAILURE, null);
            }
        });
    }

    /**
     * Load all profiles in the database (At startup)
     * Triggers DB_EVENT_PROFILE_LOADED_ON_START and DB_EVENT_PROFILE_ON_START_LOAD_FAILURE
     */
    @Override
    public void loadProfiles() {
        CollectionReference ref = dbInstance.collection(COLLECTION_PROFILE);
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<UserProfile> profiles = new ArrayList<>();
                for( QueryDocumentSnapshot doc : queryDocumentSnapshots ) {
                    UserProfile p = processProfileSnapshot(doc);
                    profiles.add(p);
                }
                sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_LOADED_ON_START, profiles);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendListenerNotification(COLLECTION_PROFILE, DatabaseListener.DB_EVENT_PROFILE_ON_START_LOAD_FAILURE, null);
            }
        });
    }

    /**
     * Generate a new experiment Id from firebase (avoids collisions, always unique - based on timestamp)
     * @return
     *      String - the Id which can be used for a new experiment
     */
    @Override
    public String getNewExperimentId() {
        CollectionReference ref = dbInstance.collection(COLLECTION_EXPERIMENT);
        return ref.document().getId();
    }

    /**
     * Generate a new profile Id from firebase (avoids collisions, always unique - based on timestamp)
     * @return
     *      String - the Id which can be used for a new profile
     */
    @Override
    public String getNewProfileId() {
        CollectionReference ref = dbInstance.collection(COLLECTION_PROFILE);
        return ref.document().getId();
    }
}

