
package com.example.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Profile> searchProfiles(String searchTerm) {
        return profileRepository.findByUsernameContainingIgnoreCase(searchTerm);
    }

    @Override
    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public Profile updateProfile(Long profileId, Profile updatedProfile) {
        return profileRepository.findById(profileId).map(profile -> {
            profile.setUsername(updatedProfile.getUsername());
            profile.setEmail(updatedProfile.getEmail());
            profile.setBio(updatedProfile.getBio());
            profile.setPrivacySettings(updatedProfile.getPrivacySettings());
            return profileRepository.save(profile);
        }).orElse(null);
    }

    @Override
    public void deleteProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new IllegalArgumentException("Profile with id " + profileId + " does not exist");
        }
        profileRepository.deleteById(profileId);
    }

    @Override
    public void updateRating(Long profileId, Double newRating) {
        profileRepository.findById(profileId).ifPresent(profile -> {
            profile.setRating(newRating);
            profileRepository.save(profile);
        });
    }
}
