package com.example.nerdherd.Deprecated;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.example.nerdherd.Model.Question;
import com.example.nerdherd.Model.Reply;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Database connection to the app
 * It controls the inflow and outflow of the data and stores that in the remote server Firestore
 * Some motivation was taken from youtube tutorials on Firestore
 * @author Zhipeng Z. zhipeng4
 * @author Tas S. saiyera
 */

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
    private Profile ownerProfile;
    private Experiment_Deprecated experimentDeprecated;
    private Boolean experimentPublish;
    private String experimentDescription;
    private String experimentType;
    private Integer experimentTrials;
    private Boolean locationRequirement;
    private HashMap<String, String> hashMapProfile;
    private ArrayList<String> idList;
    private ArrayList<HashMap> harshTrials;
    private ArrayList<Trial_Deprecated> trialDeprecateds;
    private ArrayList<Double> mTrials;
    private ArrayList<Long> nTrials;
    private ArrayList<HashMap> questionList;
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
    public void updater(String indicator, String id, String target, Object newValue, FireStoreUpdateCallback fireStoreUpdateCallback, FireStoreUpdateFailCallback fireStoreUpdateFailCallback){
        accessor(indicator);
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
    public void addNewExperiment(Experiment_Deprecated newExperimentDeprecated, FireStoreExperimentCallback fireStoreExperimentCallback, FireStoreExperimentFailCallback fireStoreExperimentFailCallback) {
        // References:
        //      https://firebase.google.com/docs/firestore/quickstart#java_1
        //      https://firebase.google.com/docs/firestore/manage-data/add-data
        // Load information into database
        String title = newExperimentDeprecated.getTitle();
        experimentData = new HashMap<>();
        experimentData.put("Status", newExperimentDeprecated.getStatus());
        experimentData.put("Published", newExperimentDeprecated.isPublished());
        experimentData.put("Description", newExperimentDeprecated.getDescription());
        experimentData.put("Type of Experiment", newExperimentDeprecated.getType());
        experimentData.put("Number of Trials", newExperimentDeprecated.getMinTrials());
        experimentData.put("Location Requirement", newExperimentDeprecated.isRequireLocation());
        experimentData.put("Owner Profile", newExperimentDeprecated.getOwnerProfile());
        experimentData.put("Owner Id", newExperimentDeprecated.getOwnerProfile().getId());
        experimentData.put("Subscriber Id", newExperimentDeprecated.getSubscriberId());
        experimentData.put("Trial List", newExperimentDeprecated.getTrials());
        Log.d("binomial test", newExperimentDeprecated.getTrials().toString());
        experimentData.put("Questions", newExperimentDeprecated.getQuestions());
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
    public void experimentReader(ArrayList<Experiment_Deprecated> experimentDeprecatedList, FireStoreExperimentReadCallback fireStoreExperimentReadCallback, FireStoreExperimentReadFailCallback fireStoreExperimentReadFailCallback){
        accessor(experimentIndicator);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    fireStoreExperimentReadFailCallback.onCallback();
                    return;
                }
                experimentDeprecatedList.clear();
                for(QueryDocumentSnapshot doc : value){
                    experimentTitle = doc.getId();
                    experimentStatus = doc.getData().get("Status").toString();
                    experimentPublish = Boolean.parseBoolean(doc.getData().get("Published").toString());
                    experimentDescription = doc.getData().get("Description").toString();
                    experimentType = doc.getData().get("Type of Experiment").toString();
                    experimentTrials = Integer.valueOf(doc.getData().get("Number of Trials").toString());
                    locationRequirement = Boolean.parseBoolean(doc.getData().get("Location Requirement").toString());
                    hashMapProfile = (HashMap<String, String>)doc.getData().get("Owner Profile");
                    idList = (ArrayList<String>) doc.getData().get("Subscriber Id");
                    harshTrials = (ArrayList<HashMap>)(doc.getData().get("Trial List"));

                    // Checks to see if our experiment 'has' questions before getting null objects as the database changes
                    Object qObj = doc.getData().get("Questions");
                    ArrayList<Question> questionArrayList = new ArrayList<>();
                    if(qObj != null) {
                        questionList = (ArrayList<HashMap>) (qObj);

                        for (HashMap questionData : questionList) {
                            Log.d("Test: ", questionData.toString());
                            String question = questionData.get("content").toString();

                            Question q = new Question(question);

                            ArrayList<String> replies = (ArrayList<String>) questionData.get("replies");

                            Log.d("Question: ", question);

                            // don't judge
                            for( String replyData : replies ) {
                                String reply = replyData;
                                Log.d("Reply: ", reply);
                                Reply r = new Reply(reply);
                                q.addReply(r);
                            }
                            questionArrayList.add(q);
                        }
                    }

                    trialDeprecateds = new ArrayList<Trial_Deprecated>();
                    for(HashMap hashTrial : harshTrials){
                        if (experimentType.equals("Binomial Trial")) {
                            BinomialTrialDeprecated binomialTrial = new BinomialTrialDeprecated(Integer.valueOf(hashTrial.get("success").toString()), Integer.valueOf(hashTrial.get("failure").toString()), hashTrial.get("timestamp").toString());
                            trialDeprecateds.add(binomialTrial);
                        }
                        if (experimentType.equals("Count")) {
                            CountTrialDeprecated countTrial = new CountTrialDeprecated(Integer.parseInt(hashTrial.get("totalCount").toString()),  hashTrial.get("timestamp").toString());

                            trialDeprecateds.add(countTrial);
                        }
                        if (experimentType.equals("Measurement trial")) {
                            Log.d("checking for sure", String.valueOf(hashTrial.get("measurements")));
                            mTrials = (ArrayList<Double>) hashTrial.get("measurements");
                            String timeStamp =  hashTrial.get("timestamp").toString();
                            Log.d("list", String.valueOf(mTrials));
                            MeasurementTrialDeprecated measurementTrial = new MeasurementTrialDeprecated(mTrials, timeStamp);
                            trialDeprecateds.add(measurementTrial);
                            Log.d("item list", String.valueOf(measurementTrial));
                        }
                        if (experimentType.equals("Non-negative trial")) {
                            nTrials = (ArrayList<Long>) hashTrial.get("nonNegativeTrials");
                            NonnegativeTrialDeprecated nonnegativeTrial = new NonnegativeTrialDeprecated(nTrials, hashTrial.get("timestamp").toString());
                            trialDeprecateds.add(nonnegativeTrial);
                        }
                    }
                    ownerProfile = new Profile(hashMapProfile.get("name"), hashMapProfile.get("password"), hashMapProfile.get("email"), hashMapProfile.get("id"), Integer.valueOf(String.valueOf(hashMapProfile.get("avatar"))));
                    experimentDeprecated = new Experiment_Deprecated(ownerProfile, experimentTitle, experimentStatus, experimentDescription, experimentType, experimentTrials, locationRequirement, experimentPublish, idList, trialDeprecateds);
                    experimentDeprecatedList.add(experimentDeprecated);

                    for( Question q : questionArrayList ) {
                        experimentDeprecated.addQuestion(q);
                    }
                }

                fireStoreExperimentReadCallback.onCallback(experimentDeprecatedList);
            }
        });
    }

    public void keepGetTrialData(ArrayList<Trial_Deprecated> itemList, String id, String type, FireStoreCertainKeepCallback fireStoreCertainKeepCallback, FireStoreCertainKeepFailCallback fireStoreCertainKeepFailCallback){
        accessor(experimentIndicator);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    fireStoreCertainKeepFailCallback.onCallback();
                    return;
                }

                itemList.clear();
                for (QueryDocumentSnapshot doc : value){
                    if(doc.getId().equals(id)){
                        for(HashMap hashTrial : (ArrayList<HashMap>)(doc.getData().get("Trial List"))){
                            if (type.equals("Binomial")) {
                                BinomialTrialDeprecated binomialTrial = new BinomialTrialDeprecated(Integer.valueOf(hashTrial.get("success").toString()), Integer.valueOf(hashTrial.get("failure").toString()), hashTrial.get("timestamp").toString());
                                itemList.add(binomialTrial);
                            }
                            if (type.equals("Count trial")) {
                                CountTrialDeprecated countTrial = new CountTrialDeprecated(Integer.parseInt(hashTrial.get("totalCount").toString()),  hashTrial.get("timestamp").toString());
                                itemList.add(countTrial);
                            }

                            if (type.equals("Measurement trial")) {
                                Log.d("checking for sure", String.valueOf(hashTrial.get("measurements")));
                                mTrials = (ArrayList<Double>) hashTrial.get("measurements");
                                String timeStamp =  hashTrial.get("timestamp").toString();
                                Log.d("list", String.valueOf(mTrials));
                                MeasurementTrialDeprecated measurementTrial = new MeasurementTrialDeprecated(mTrials, timeStamp);
                                itemList.add(measurementTrial);
                                Log.d("item list", String.valueOf(measurementTrial));
                            }
                            if (type.equals("Non-negative trial")) {
                                Log.d("checking for sure", String.valueOf(hashTrial.get("nonNegativeTrials")));
                                nTrials = (ArrayList<Long>) hashTrial.get("nonNegativeTrials");
//                                for (int y = 0; y < nTrials.size(); y++){
//                                    int x = Math.toIntExact(nTrials.get(y));
//                                    nTrials.set(y, x);
//                                }
                                Log.d("list", String.valueOf(nTrials));
                                NonnegativeTrialDeprecated nonnegativeTrial= new NonnegativeTrialDeprecated(nTrials, hashTrial.get("timestamp").toString());
                                itemList.add(nonnegativeTrial);
                                Log.d("item list", String.valueOf(nonnegativeTrial));
                            }
                        }
                    }
                }
                fireStoreCertainKeepCallback.onCallback(itemList);
            }
        });
    }

    public void createExperimentReader(ArrayList<Experiment_Deprecated> experimentDeprecatedList, FireStoreCreateExperimentReadCallback fireStoreCreateExperimentReadCallback, FireStoreCreateExperimentReadFailCallback fireStoreCreateExperimentReadFailCallback){
        accessor(experimentIndicator);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    experimentDeprecatedList.clear();
                    for(QueryDocumentSnapshot doc : task.getResult()) {
                        experimentTitle = doc.getId();
                        experimentStatus = doc.getData().get("Status").toString();
                        experimentPublish = Boolean.parseBoolean(doc.getData().get("Published").toString());
                        experimentDescription = doc.getData().get("Description").toString();
                        experimentType = doc.getData().get("Type of Experiment").toString();
                        experimentTrials = Integer.valueOf(doc.getData().get("Number of Trials").toString());
                        locationRequirement = Boolean.parseBoolean(doc.getData().get("Location Requirement").toString());
                        hashMapProfile = (HashMap<String, String>) doc.getData().get("Owner Profile");
                        idList = (ArrayList<String>) doc.getData().get("Subscriber Id");

                        ownerProfile = new Profile(hashMapProfile.get("name"), hashMapProfile.get("password"), hashMapProfile.get("email"), hashMapProfile.get("id"), Integer.valueOf(String.valueOf(hashMapProfile.get("avatar"))));
                        experimentDeprecated = new Experiment_Deprecated(ownerProfile, experimentTitle, experimentStatus, experimentDescription, experimentType, experimentTrials, locationRequirement, experimentPublish, idList, new ArrayList<Trial_Deprecated>());
                        experimentDeprecatedList.add(experimentDeprecated);
                    }
                    fireStoreCreateExperimentReadCallback.onCallback(experimentDeprecatedList);
                }
                else{
                    fireStoreCreateExperimentReadFailCallback.onCallback();
                }
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
        void onCallback(ArrayList<Experiment_Deprecated> experimentDeprecateds);
    }
    public interface FireStoreExperimentReadFailCallback {
        void onCallback();
    }
    public interface FireStoreCreateExperimentReadCallback{
        void onCallback(ArrayList<Experiment_Deprecated> experimentDeprecateds);
    }
    public interface FireStoreCreateExperimentReadFailCallback {
        void onCallback();
    }
    public interface FireStoreCertainKeepCallback{
        void onCallback(ArrayList<Trial_Deprecated> list);
    }
    public interface FireStoreCertainKeepFailCallback{
        void onCallback();
    }
}