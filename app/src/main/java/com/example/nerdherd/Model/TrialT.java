package com.example.nerdherd.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class TrialT {
    private String experimenterId;
    private float outcome;
    private GeoPoint location;
    private Timestamp date;

    public TrialT() {

    }

    public TrialT(String experimenterId, float outcome, GeoPoint location, Timestamp date) {
        this.experimenterId = experimenterId;
        this.outcome = outcome;
        this.location = location;
        this.date = date;
    }

    public String getExperimenterId() {
        return experimenterId;
    }

    public void setExperimenterId(String experimenterId) {
        this.experimenterId = experimenterId;
    }

    public double getOutcome() {
        return outcome;
    }

    public void setOutcome(float outcome) {
        this.outcome = outcome;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
