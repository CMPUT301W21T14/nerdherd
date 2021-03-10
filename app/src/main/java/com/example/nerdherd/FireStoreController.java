package com.example.nerdherd;

// Modified from youtube
// Author: Zhipeng Z zhipeng4

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FireStoreController {

    private static final String TAG = "FireStoreController";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private String existed;
    private String existedId;
    private String existedPassword;
    private Pair<String, String> infoPair;
    private HashMap<String, Object> profileData;
    private HashMap<String, Object> experimentData;
    private DocumentSnapshot doc;
    private String name;
    private String password;
    private String email;
    private ProfileController profileController;
    private Integer avatar;
    private String profileIndicator = "Profile";
    private String experimentIndicator = "Experiment";
    private String experimentTitle;
    private String experimentStatus;
    private String experimentOwner;
    private ArrayList<Profile> ownerProfile;
    private String ownerId;
    private Experiment experiment;
    private Boolean experimentPublish;
    private String experimentDescription;
    private String experimentType;
    private Integer experimentTrials;
    private Boolean locationRequirement;
    private Profile profile;

    private void accessor(String indicator){
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(indicator);
    }

    public void readData(ArrayList<String> itemList, String verifier, FireStoreReadCallback fireStoreReadCallback, FireStoreReadFailCallback fireStoreReadFailCallback){
        itemList.clear();
        accessor(profileIndicator);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        if (verifier.equals("Id")){
                            existed = doc.getId();
                        }
                        else{
                            existed = doc.getString(verifier);
                        }
                        itemList.add(existed);
                    }
                    fireStoreReadCallback.onCallback(itemList);
                }
                else{
                    fireStoreReadFailCallback.onCallback();
                }
            }
        });
    }

    public void uploadData(Profile profile, String id, FireStoreUploadCallback fireStoreUploadCallback, FireStoreUploadFailCallback fireStoreUploadFailCallback){
        accessor(profileIndicator);
        profileData = new HashMap<>();
        profileData.put("Name", profile.getName());
        profileData.put("Password", profile.getPassword());
        profileData.put("Email", profile.getEmail());
        profileData.put("Avatar", profile.getAvatar());
        collectionReference
                .document(id)
                .set(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fireStoreUploadCallback.onCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fireStoreUploadFailCallback.onCallback();
                    }
                });
    }

    public void logInCheck(ArrayList<Pair> pairList, FireStoreCheckCallback fireStoreCheckCallback, FireStoreCheckFailCallback fireStoreCheckFailCallback){
        accessor(profileIndicator);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        existedId = doc.getId();
                        existedPassword = doc.getString("Password");
                        infoPair = new Pair<String, String>(existedId, existedPassword);
                        pairList.add(infoPair);
                    }
                    fireStoreCheckCallback.onCallback(pairList);
                }
                else{
                    fireStoreCheckFailCallback.onCallback();
                }
            }
        });
    }

    //private arrayList<String> return_val
    //sps = new ArrayList<String>
    //field: email
    //value: osamuel@ualberta.ca - can make val public
    //getCertainData(sps, email, 'osamuel@ualberta.ca', 'username',

    public void getCertainData(ArrayList<String> itemList, String field, String value, String wanted, FireStoreCertainCallback fireStoreCertainCallback, FireStoreCertainFailCallback fireStoreCertainFailCallback){
        accessor(profileIndicator);
        collectionReference.whereEqualTo(field, value).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        if (wanted.equals("Id")){
                            itemList.add(doc.getId());
                        }
                        else{
                            itemList.add(doc.getString(wanted));
                        }
                    }
                    fireStoreCertainCallback.onCallback(itemList);
                }
                else{
                    fireStoreCertainFailCallback.onCallback();
                }
            }
        });
    }

    public void updater(String id, String target, String newValue, FireStoreUpdateCallback fireStoreUpdateCallback, FireStoreUpdateFailCallback fireStoreUpdateFailCallback){
        accessor(profileIndicator);
        collectionReference.document(id).update(target, newValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fireStoreUpdateCallback.onCallback();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fireStoreUpdateFailCallback.onCallback();
            }
        });
    }

    public void readProfile(ArrayList<Profile> profileList, String id, String indicator, FireStoreProfileCallback fireStoreProfileCallback, FireStoreProfileFailCallback fireStoreProfileFailCallback, FireStoreProfileListCallback fireStoreProfileListCallback, FireStoreProfileListFailCallback fireStoreProfileListFailCallback){
        accessor(profileIndicator);
        if (indicator.equals("Current User")) {
            collectionReference.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        doc = task.getResult();
                        name = doc.getData().get("Name").toString();
                        password = doc.getData().get("Password").toString();
                        email = doc.getData().get("Email").toString();
                        avatar = Integer.valueOf(doc.getData().get("Avatar").toString());
                        fireStoreProfileCallback.onCallback(name, password, email, avatar);
                    } else {
                        fireStoreProfileFailCallback.onCallback();
                    }
                }
            });
        }
        if (indicator.equals("All User")){
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null){
                        fireStoreProfileListFailCallback.onCallback();
                        return;
                    }

                    profileList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        name = doc.getData().get("Name").toString();
                        password = doc.getData().get("Password").toString();
                        email = doc.getData().get("Email").toString();
                        avatar = Integer.valueOf(doc.getData().get("Avatar").toString());
                        profileController = new ProfileController(name, password, email, doc.getId(), avatar);
                        profileController.creator();
                        profileList.add(profileController.getProfile());
                    }
                    fireStoreProfileListCallback.onCallback(profileList);
                }
            });
        }
    }

    public void addNewExperiment(Experiment newExperiment, FireStoreExperimentCallback fireStoreExperimentCallback, FireStoreExperimentFailCallback fireStoreExperimentFailCallback) {
        // References:
        //      https://firebase.google.com/docs/firestore/quickstart#java_1
        //      https://firebase.google.com/docs/firestore/manage-data/add-data

        // Load information into database
        String title = newExperiment.getTitle();
        experimentData = new HashMap<>();
        experimentData.put("Owner ID", newExperiment.getOwnerProfile().getId());
        experimentData.put("Status", newExperiment.getStatus());
        experimentData.put("Published", newExperiment.isPublished());
        experimentData.put("Description", newExperiment.getDescription());
        experimentData.put("Type of Experiment", newExperiment.getType());
        experimentData.put("Number of Trials", newExperiment.getMinTrials());
        experimentData.put("Location Requirement", newExperiment.isRequireLocation());

        // Get user ID
        String userID = newExperiment.getOwnerProfile().getId();

        // Find user information and load data
        accessor(experimentIndicator);
        collectionReference
                .document(title)
                .set(experimentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fireStoreExperimentCallback.onCallback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fireStoreExperimentFailCallback.onCallback();
                    }
                });
    }

    public void experimentReader(ArrayList<Experiment> experimentList, FireStoreExperimentReadCallback fireStoreExperimentReadCallback, FireStoreExperimentReadFailCallback fireStoreExperimentReadFailCallback){
        accessor(experimentIndicator);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    fireStoreExperimentReadFailCallback.onCallback();
                    return;
                }

                experimentList.clear();
                for(QueryDocumentSnapshot doc : value){
                    experimentTitle = doc.getId();
                    experimentStatus = doc.getData().get("Status").toString();
                    ownerId = doc.getData().get("Owner ID").toString();
                    experimentPublish = Boolean.parseBoolean(doc.getData().get("Published").toString());
                    experimentDescription = doc.getData().get("Description").toString();
                    experimentType = doc.getData().get("Type of Experiment").toString();
                    experimentTrials = Integer.valueOf(doc.getData().get("Number of Trials").toString());
                    locationRequirement = Boolean.parseBoolean(doc.getData().get("Location Requirement").toString());

                    experiment = new Experiment(ownerId, experimentTitle, experimentStatus, experimentDescription, experimentType, experimentTrials, locationRequirement, experimentPublish);
                    experimentList.add(experiment);
                }
                
                fireStoreExperimentReadCallback.onCallback(experimentList);
            }
        });
    }

    public void formalExperimentReader(ArrayList<Experiment> expList, ArrayList<Experiment> saved, FireStoreFormalReadCallback fireStoreFormalReadCallback, FireStoreFormalReadFailCallback fireStoreFormalReadFailCallback){
        experimentReader(expList, new FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {
                saved.clear();
                for (Experiment exp : experiments){
                    ownerId = exp.getOwnerId();
                    readProfile(ownerProfile, ownerId, "Current User", new FireStoreProfileCallback() {
                        @Override
                        public void onCallback(String name, String password, String email, Integer avatar) {
                            profile = new Profile(name, password, email, ownerId, avatar);
                            experiment = new Experiment(profile, exp.getTitle(), exp.getStatus(), exp.getDescription(), exp.getType(), exp.getMinTrials(), exp.isRequireLocation(), exp.isPublished());
                            saved.add(experiment);
                            fireStoreFormalReadCallback.onCallback(saved);
                        }
                    }, new FireStoreProfileFailCallback() {
                        @Override
                        public void onCallback() {
                            fireStoreFormalReadFailCallback.onCallback();
                        }
                    }, null, null);
                }
            }
        }, new FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                fireStoreFormalReadFailCallback.onCallback();
            }
        });
    }

    public interface FireStoreReadCallback{
        void onCallback(ArrayList<String> list);
    }

    public interface FireStoreUploadCallback{
        void onCallback();
    }

    public interface FireStoreReadFailCallback{
        void onCallback();
    }

    public interface FireStoreUploadFailCallback{
        void onCallback();
    }

    public interface FireStoreCheckCallback{
        void onCallback(ArrayList<Pair> list);
    }

    public interface FireStoreCheckFailCallback{
        void onCallback();
    }

    public interface FireStoreCertainCallback{
        void onCallback(ArrayList<String> list);
    }

    public interface FireStoreCertainFailCallback{
        void onCallback();
    }

    public interface FireStoreUpdateCallback{
        void onCallback();
    }

    public interface FireStoreUpdateFailCallback{
        void onCallback();
    }

    public interface FireStoreProfileCallback{
        void onCallback(String name, String password, String email, Integer avatar);
    }

    public interface FireStoreProfileFailCallback {
        void onCallback();
    }

    public interface FireStoreProfileListCallback{
        void onCallback(ArrayList<Profile> profileList);
    }

    public interface FireStoreProfileListFailCallback {
        void onCallback();
    }

    public interface FireStoreExperimentCallback{
        void onCallback();
    }

    public interface FireStoreExperimentFailCallback {
        void onCallback();
    }

    public interface FireStoreExperimentReadCallback{
        void onCallback(ArrayList<Experiment> experiments);
    }

    public interface FireStoreExperimentReadFailCallback {
        void onCallback();
    }

    public interface FireStoreFormalReadCallback{
        void onCallback(ArrayList<Experiment> experiments);
    }

    public interface FireStoreFormalReadFailCallback {
        void onCallback();
    }
}
