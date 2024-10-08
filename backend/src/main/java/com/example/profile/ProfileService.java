package com.example.profile;

import java.util.Optional;

public interface ProfileService {
    Optional<Profile> getProfile(Long profileId);
    Profile updateProfile(Long profileId, Profile updatedProfile);
}
