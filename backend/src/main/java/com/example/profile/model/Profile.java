package com.example.profile.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "profiles")  // This is optional. You can customize the table name.
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // You need an id field for the entity to be managed

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    private String bio;
    private String privacySettings;

    // Default constructor (required by JPA)
    public Profile() {}

    // Constructor with parameters
    public Profile(String username, String email, String bio, String privacySettings) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.privacySettings = privacySettings;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPrivacySettings() {
        return privacySettings;
    }

    public void setPrivacySettings(String privacySettings) {
        this.privacySettings = privacySettings;
    }
}
