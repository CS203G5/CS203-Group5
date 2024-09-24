package com.example.profile.service;

import com.example.profile.model.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private Profile profile = new Profile("user123", "user@example.com", "Bio text", "Public");

    // Fetch the profile
    public Profile getProfile() {
        return profile;
    }

    // Update the profile
    public Profile updateProfile(Profile newProfile) {
        profile.setUsername(newProfile.getUsername());
        profile.setEmail(newProfile.getEmail());
        profile.setBio(newProfile.getBio());
        profile.setPrivacySettings(newProfile.getPrivacySettings());
        return profile;
    }
}
