
package com.example.profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    Optional<Profile> getProfile(Long profileId);

    List<Profile> searchProfiles(String searchTerm);

    Profile saveProfile(Profile profile);

    Optional<Profile> updateProfile(Long profileId, Profile updatedProfile);

    Optional<Profile> getProfileByUsername(String username);
    
    void deleteProfile(Long profileId);

    void updateRating(Long profileId, Double newRating);
}
