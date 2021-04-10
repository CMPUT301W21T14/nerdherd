package com.example.nerdherd.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

/**
 * Trial object associated with different experiments
 * Only one type of Trial class as the 'outcome' is singular for all of them
 */
public class Trial {
    private String experimenterId;
    private float outcome;
    private GeoPoint location;
    private Timestamp date;

    public Trial() {

    }

    /**
     * TrialT object constructor
     * @param experimenterId
     *      String - experimentId who created/ran this trial
     * @param outcome
     *      Float - outcome of the trial. Different depending on type of trial dictated by experiment
     * @param location
     *      GeoPoint - firebase location object consisting of lattitude and longitude
     * @param date
     *      Timestamp - firebase date object indicated when the experiment was run
     */
    public Trial(String experimenterId, float outcome, GeoPoint location, Timestamp date) {
        this.experimenterId = experimenterId;
        this.outcome = outcome;
        this.location = location;
        this.date = date;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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
