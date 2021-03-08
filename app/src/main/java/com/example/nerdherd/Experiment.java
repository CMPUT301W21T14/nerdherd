package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

public class Experiment {
    private String owner;
    private String status;
    private String title;
    private String type;
    private int minTrials;
    private boolean requireLocation = false;
    private boolean published = true;


    public Experiment(String owner, String status, String title) {
        this.owner = owner;
        this.status = status;
        this.title = title;
    }

    public Experiment(String owner, String status, String title, String type, int minTrials, boolean requireLocation, boolean published) {
        this.owner = owner;
        this.status = status;
        this.title = title;
        this.type = type;
        this.minTrials = minTrials;
        this.requireLocation = requireLocation;
        this.published = published;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getMinTrials() { return minTrials; }
    public void setMinTrials(int minTrials) { this.minTrials = minTrials; }

    public boolean isRequireLocation() { return requireLocation; }
    public void setRequireLocation(boolean requireLocation) { this.requireLocation = requireLocation; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
