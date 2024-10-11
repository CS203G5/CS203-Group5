
package com.example.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{profileId}")
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

    @PutMapping("/{profileId}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long profileId, @Valid @RequestBody Profile updatedProfile) {
        Profile profile = profileService.updateProfile(profileId, updatedProfile);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
