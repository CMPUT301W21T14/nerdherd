package com.example.nerdherd;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class FireStoreController {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private String existed;
    private HashMap<String, Object> profileData;

    public void readData(ArrayList<String> itemList, String verifier, FireStoreReadCallback fireStoreReadCallback, FireStoreReadFailCallback fireStoreReadFailCallback){
        itemList.clear();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Profile");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        if (verifier.equals("Email")) {
                            existed = doc.getString("Email");
                        }
                        if (verifier.equals("Id")){
                            existed = doc.getId();
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
}
