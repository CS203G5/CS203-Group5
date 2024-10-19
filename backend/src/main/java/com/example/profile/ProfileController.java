
package com.example.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{profile_id}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long profileId) {
        return profileService.getProfile(profileId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Profile>> searchProfiles(@RequestParam String searchTerm) {
        List<Profile> profiles = profileService.searchProfiles(searchTerm);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(@Valid @RequestBody Profile profile) {
        Profile createdProfile = profileService.saveProfile(profile);
        return ResponseEntity.ok(createdProfile);
    }

    @PutMapping("/{profile_id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long profileId, @Valid @RequestBody Profile updatedProfile) {
        return profileService.updateProfile(profileId, updatedProfile)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{profile_id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{profile_id}/rating")
    @PreAuthorize("hasRole('ADMIN')") // Ensure only admins can access this endpoint
    public ResponseEntity<Profile> updateRating(@PathVariable Long profileId, @RequestParam Double newRating) {
        Optional<Profile> updatedProfile = profileService.getProfile(profileId);
        if (updatedProfile.isPresent()) {
            profileService.updateRating(profileId, newRating);
            return ResponseEntity.ok(updatedProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Profile> getProfileByUsername(@PathVariable String username) {
        return profileService.getProfileByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
