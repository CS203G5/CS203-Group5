
package com.example.profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    Optional<Profile> getProfile(Long profileId);

    List<Profile> searchProfiles(String searchTerm);

    Profile saveProfile(Profile profile);

    Profile updateProfile(Long profileId, Profile updatedProfile);

    void deleteProfile(Long profileId);
}
