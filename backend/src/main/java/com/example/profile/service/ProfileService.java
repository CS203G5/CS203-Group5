package com.example.profile.service;

import com.example.profile.model.Profile;
import com.example.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public Profile getProfile(String username) {
        return profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile updateProfile(Profile newProfile) {
        Profile existingProfile = profileRepository.findByUsername(newProfile.getUsername())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        existingProfile.setEmail(newProfile.getEmail());
        existingProfile.setBio(newProfile.getBio());
        existingProfile.setPrivacySettings(newProfile.getPrivacySettings());

        return profileRepository.save(existingProfile);
    }
}
