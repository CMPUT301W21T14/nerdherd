package com.example.nerdherd.Model;

import com.example.nerdherd.Question;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class ExperimentE {
    public static final int EXPERIMENT_TYPE_BINOMIAL = 0;
    public static final int EXPERIMENT_TYPE_COUNT = 1;
    public static final int EXPERIMENT_TYPE_NON_NEGATIVE = 2;
    public static final int EXPERIMENT_TYPE_MEASUREMENT = 3;

    private String experimentId;
    private String ownerId;
    private String description;
    private String title;
    private Region region;
    private int minimumTrials;
    private boolean published;
    private boolean locationRequired;
    private String status;
    private int type;
    private Timestamp date;
    private ArrayList<Question> questions;
    private ArrayList<TrialT> trials;
    private ArrayList<String> userIdBlacklist;

    public ExperimentE() {
        // Default constructor for serialization
    }

    public ExperimentE(String experimentId, String title, String description, String ownerId, Region region, int minimumTrials, int type, Timestamp date, boolean locationRequired, boolean published) {
        this.experimentId = experimentId;
        this.description = description;
        this.ownerId = ownerId;
        this.region = region;
        this.minimumTrials = minimumTrials;
        this.type = type;
        this.date = date;
        this.published = published;
        this.locationRequired = locationRequired;
        this.title = title;
        this.status="Open";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLocationRequired() {
        return locationRequired;
    }

    public void setLocationRequired(boolean locationRequired) {
        this.locationRequired = locationRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getMinimumTrials() {
        return minimumTrials;
    }

    public void setMinimumTrials(int minimumTrials) {
        this.minimumTrials = minimumTrials;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public String typeToString() {
        switch(type) {
            case EXPERIMENT_TYPE_BINOMIAL:
                return "Binomial";
            case EXPERIMENT_TYPE_COUNT:
                return "Count";
            case EXPERIMENT_TYPE_MEASUREMENT:
                return "Measurement";
            case EXPERIMENT_TYPE_NON_NEGATIVE:
                return "Non-Negative";
        }
        return "Invalid Type";
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Question> getQuestions() {
        if(questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<TrialT> getTrials() {
        return trials;
    }

    public void setTrials(ArrayList<TrialT> trials) {
        this.trials = trials;
    }

    public ArrayList<String> getUserIdBlacklist() {
        return userIdBlacklist;
    }

    public void setUserIdBlacklist(ArrayList<String> userIdBlacklist) {
        this.userIdBlacklist = userIdBlacklist;
    }

    public void addToBlacklist(String userId) {
        // Check if loaded from database without any blacklist
        if(userIdBlacklist == null) {
            userIdBlacklist = new ArrayList<>();
        }

        if(!userIdBlacklist.contains(userId)) {
            this.userIdBlacklist.add(userId);
        }
    }

    public void addQuestion(Question question) {
        if(questions == null) {
            questions = new ArrayList<>();
        }
        if(!questions.contains(question)) {
            questions.add(question);
        }
    }

    public void addTrial(TrialT trial) {
        if(trials == null) {
            trials = new ArrayList<>();
        }
        trials.add(trial);
    }
}
