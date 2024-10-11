package com.example.unit.profile;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import com.example.profile.Profile;
import com.example.profile.ProfileRepository;
import com.example.profile.ProfileServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    public void testGetProfile() {
        // Given
        Long profileId = 1L;
        Profile mockProfile = new Profile(1L, "user1", "user1@example.com", "Bio", "Public");
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(mockProfile));

        // When
        Optional<Profile> result = profileService.getProfile(profileId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUsername());
        verify(profileRepository).findById(profileId);
    }

    @Test
    public void testUpdateProfile() {
        // Given
        Long profileId = 1L;
        Profile existingProfile = new Profile(1L, "user1", "user1@example.com", "Bio", "Public");
        Profile updatedProfile = new Profile(1L, "user1", "user2@example.com", "New Bio", "Private");

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.findByEmail(updatedProfile.getEmail())).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);

        // When
        Profile result = profileService.updateProfile(profileId, updatedProfile);

        // Then
        assertEquals("user2@example.com", result.getEmail());
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(existingProfile);
    }

    @Test
    public void testUpdateProfile_EmailAlreadyInUse() {
        // Given
        Long profileId = 1L;
        Profile existingProfile = new Profile(1L, "user1", "user1@example.com", "Bio", "Public");
        Profile updatedProfile = new Profile(1L, "user1", "user2@example.com", "New Bio", "Private");

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.findByEmail(updatedProfile.getEmail())).thenReturn(Optional.of(new Profile())); // Simulate existing email

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            profileService.updateProfile(profileId, updatedProfile);
        });
        assertEquals("Email is already in use.", thrown.getMessage());
    }

    @Test
    public void testUpdateProfile_ProfileNotFound() {
        // Given
        Long profileId = 1L;
        Profile updatedProfile = new Profile(1L, "user1", "user2@example.com", "New Bio", "Private");

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            profileService.updateProfile(profileId, updatedProfile);
        });
        assertEquals("Profile not found", thrown.getMessage());
    }
}
