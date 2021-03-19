package com.example.nerdherd;

// Author: Zhipeng Z zhipeng4

import android.graphics.Bitmap;

/**
 * User profile
 * This has all the neccessary requirements of any profile
 * @author Zhipeng Z. zhipeng4
 * @author Ogooluwa S. osamuel
 */

public class Profile {
    private String name;
    private String password;
    private String email;
    private Integer avatar;
    private String id;

    /**
     * Specific requirements of the profile
     * Getter/setter of the class
     * @param name of the user
     * @param password of the user
     * @param email of the user
     * @param id of the user
     * @param avatar of the user
     */

    public Profile(String name, String password, String email, String id, Integer avatar) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.avatar = avatar;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId(){return id;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }
}


