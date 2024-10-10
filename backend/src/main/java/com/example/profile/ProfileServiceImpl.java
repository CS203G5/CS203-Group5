package com.example.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Optional<Profile> getProfile(Long profileId) {
        return profileRepository.findById(profileId);
    }

    @Override
    @Transactional
    public Profile updateProfile(Long profileId, Profile updatedProfile) {
        Profile existingProfile = profileRepository.findById(profileId).orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Update fields with validation logic
        if (!existingProfile.getEmail().equals(updatedProfile.getEmail())) {
            if (profileRepository.findByEmail(updatedProfile.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already in use.");
            }
        }

        existingProfile.setUsername(updatedProfile.getUsername());
        existingProfile.setEmail(updatedProfile.getEmail());
        existingProfile.setBio(updatedProfile.getBio());
        existingProfile.setPrivacySettings(updatedProfile.getPrivacySettings());

        return profileRepository.save(existingProfile);
    }
}
