package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

/*It gives all the possible details an Experiment possess like owner of that Experiment, title etc.
* This defines the specificity per Experiment.
* */

import java.io.Serializable;
import java.util.ArrayList;

public class Experiment implements Serializable {

    private Profile ownerProfile;
    private String status;
    private String title;
    private String description;
    private String type;
    private int minTrials;
    private boolean requireLocation = false;
    private boolean published = true;
    private ArrayList<String> subscriberId;
    private ArrayList<Trial> trials;


    public Experiment(Profile profile, String title, String status, String description, String type, int minTrials, boolean requireLocation, boolean published, ArrayList<String> subscriberId, ArrayList<Trial> trials) {
        // Let's allow to create Experiment using owner ID only
        // Search classes can search for owner

        this.ownerProfile = profile;
        this.title = title;
        this.status = status;
        this.description = description;
        this.type = type;
        this.minTrials = minTrials;
        this.requireLocation = requireLocation;
        this.published = published;
        this.subscriberId = subscriberId;
        this.trials = trials;
    }

    public ArrayList<Trial> getTrials() {
        return trials;
    }

    public void setTrials(ArrayList<Trial> trials) {
        this.trials = trials;
    }
    // Let's use IDs for Experiment class
    /*
    public Profile getOwnerProfile() { return ownerProfile; }
    public void setOwnerProfile(Profile ownerProfile) { this.ownerProfile = ownerProfile; }
     */

    public Profile getOwnerProfile() {
        return ownerProfile;
    }

    public void setOwnerProfile(Profile profile) {
        this.ownerProfile = profile;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getMinTrials() { return minTrials; }
    public void setMinTrials(int minTrials) { this.minTrials = minTrials; }

    public boolean isRequireLocation() { return requireLocation; }
    public void setRequireLocation(boolean requireLocation) { this.requireLocation = requireLocation; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public ArrayList<String> getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(ArrayList<String> subscriberId) {
        this.subscriberId = subscriberId;
    }
}
