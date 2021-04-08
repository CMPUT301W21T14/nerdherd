package com.example.nerdherd.Model;

public class UserProfile {
    private String userName;
    private String userId;
    private String contactInfo;

    public UserProfile() {
        // Empty constructor for serialization
    }

    public UserProfile(String userId, String userName, String contactInfo) {
        this.userId=userId;
        this.userName=userName;
        this.contactInfo=contactInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
