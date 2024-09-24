package com.example.profile.controller;

import com.example.profile.model.Profile;
import com.example.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // Get profile details
    @GetMapping
    public Profile getProfile() {
        return profileService.getProfile();
    }

    // Update profile details
    @PutMapping
    public Profile updateProfile(@RequestBody Profile profile) {
        return profileService.updateProfile(profile);
    }
}
