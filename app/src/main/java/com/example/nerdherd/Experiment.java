package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import java.io.Serializable;

public class Experiment implements Serializable {
    private String owner; //TODO This shouldn't be used, name should be taken from the profile object

    private Profile ownerProfile;
    private String status;
    private String title;
    private String description;
    private String type;
    private int minTrials;
    private boolean requireLocation = false;
    private boolean published = true;


    public Experiment(String owner, String status, String title) {
        this.owner = owner;
        this.status = status;
        this.title = title;
    }

    public Experiment(Profile ownerProfile, String title, String status, String description, String type, int minTrials, boolean requireLocation, boolean published) {
        this.ownerProfile = ownerProfile;
        this.title = title;
        this.status = status;
        this.description = description;
        this.type = type;
        this.minTrials = minTrials;
        this.requireLocation = requireLocation;
        this.published = published;
    }

    public String getOwner() {
        return owner;
    } //Do not use

    public void setOwner(String owner) {
        this.owner = owner;
    } //Do not use

    public Profile getOwnerProfile() { return ownerProfile; }
    public void setOwnerProfile(Profile ownerProfile) { this.ownerProfile = ownerProfile; }

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
}
