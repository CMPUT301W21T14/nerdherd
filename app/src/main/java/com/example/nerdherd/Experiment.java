package com.example.nerdherd;

public class Experiment {
    private String owner;
    private String status;
    private String title;

    public Experiment(String owner, String status, String title) {
        this.owner = owner;
        this.status = status;
        this.title = title;
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
}
