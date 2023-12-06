package com.developerobaida.postcraft.model;

public class User {
    private String email;
    private String phoneNumber;
    private String displayName;
    private String password;
    private String imageUrl;

    // Required default constructor for Firebase
    public User() {
    }

    public User(String email, String phoneNumber, String displayName, String password,String imageUrl) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
        this.password = password;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
