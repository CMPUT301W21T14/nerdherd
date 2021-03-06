package com.example.nerdherd.Deprecated;

import com.example.nerdherd.Model.Question;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Specificity per experiment
 * It gives all the possible details an Experiment possess like owner of that Experiment, title etc for the owner to set as per their needs
 * @author Zhipeng Z. zhipeng4
 */

public class Experiment_Deprecated implements Serializable {

    private Profile ownerProfile;
    private String status;
    private String title;
    private String description;
    private String type;
    private int minTrials;
    private boolean requireLocation = false;
    private boolean published = true;
    private ArrayList<String> subscriberId;
    private ArrayList<Trial_Deprecated> trialDeprecateds;
    private ArrayList<Question> questions = new ArrayList<Question>();

    /**
     * Experiment describer
     * The owners can tweak their experiment based on their needs
     * @param profile owner of the experiment
     * @param title the name of the experiment
     * @param status boolean stating activity of the app
     * @param description brief explanation of the app purpose
     * @param type the kind of trial
     * @param minTrials specify a number
     * @param requireLocation geolocation availability
     * @param published boolean confirmation of it being visible to the users
     * @param subscriberId
     * @param trialDeprecateds
     * @author Zhipeng Z. zhipeng 4
     * @author Andrew D. adearbor
     * @author Tas S. saiyera
     */

    public Experiment_Deprecated(Profile profile, String title, String status, String description, String type, int minTrials, boolean requireLocation, boolean published, ArrayList<String> subscriberId, ArrayList<Trial_Deprecated> trialDeprecateds) {
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
        this.trialDeprecateds = trialDeprecateds;
        this.questions = new ArrayList<>();
    }

    public ArrayList<Trial_Deprecated> getTrials() {
        return trialDeprecateds;
    }

    public void setTrials(ArrayList<Trial_Deprecated> trialDeprecateds) {
        this.trialDeprecateds = trialDeprecateds;
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
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
}
