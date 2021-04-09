package com.example.nerdherd.Model;

/**
 * Object representing the details associated with a User who uses the application
 */
public class UserProfile {
    private String userName;
    private String userId;
    private String contactInfo;
    private int avatarId;

    public UserProfile() {
        // Empty constructor for serialization
    }

    /**
     * UserProfile constructor
     * @param userId
     *      String - unique Id of this specific user - stored in LocalUser
     * @param userName
     *      String - unique username of the user - can be set
     * @param contactInfo
     *      String - contact information of the user, perhaps an email
     */
    public UserProfile(String userId, String userName, String contactInfo) {
        this.userId=userId;
        this.userName=userName;
        this.contactInfo=contactInfo;
        this.avatarId=0;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
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
