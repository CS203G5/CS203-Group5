package com.example.profile.model;

public class Profile {

    private String username;
    private String email;
    private String bio;
    private String privacySettings;

    // Constructors, Getters, and Setters

    public Profile() {}

    public Profile(String username, String email, String bio, String privacySettings) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.privacySettings = privacySettings;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(String privacySettings) { this.privacySettings = privacySettings; }
}
