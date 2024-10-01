package com.example.profile.controller;

import com.example.profile.model.Profile;
import com.example.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // Get profile by username (passed as query parameter)
    @GetMapping
    public Profile getProfile(@RequestParam String username) {
        return profileService.getProfile(username);
    }

    // Update profile details with validation
    @PutMapping
    public Profile updateProfile(@Valid @RequestBody Profile profile) {
        return profileService.updateProfile(profile);
    }
}
