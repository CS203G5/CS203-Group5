package com.example.unit.profile;

import com.example.profile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProfile_Success_ReturnsProfile() {
        // Arrange
        Long profileId = 1L;
        Profile profile = new Profile(profileId, "testuser", "test@example.com", "Bio", "private", 0.0, "USER");
        
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        Optional<Profile> foundProfile = profileService.getProfile(profileId);

        // Assert
        assertTrue(foundProfile.isPresent());
        assertEquals(profileId, foundProfile.get().getProfileId());
        verify(profileRepository).findById(profileId);
    }

    @Test
    void getProfile_NotFound_ReturnsEmptyOptional() {
        // Arrange
        Long profileId = 1L;
        
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act
        Optional<Profile> foundProfile = profileService.getProfile(profileId);

        // Assert
        assertFalse(foundProfile.isPresent());
        verify(profileRepository).findById(profileId);
    }

    @Test
    void searchProfiles_Success_ReturnsListOfProfiles() {
        // Arrange
        String searchTerm = "user";
        Profile profile1 = new Profile(1L, "user1", "user1@example.com", null, "private", 0.0, "USER");
        Profile profile2 = new Profile(2L, "user2", "user2@example.com", null, "private", 0.0, "USER");
        
        when(profileRepository.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList(profile1, profile2));

        // Act
        List<Profile> profiles = profileService.searchProfiles(searchTerm);

        // Assert
        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        verify(profileRepository).findByUsernameContainingIgnoreCase(searchTerm);
    }

    @Test
    void saveProfile_Success_ReturnsSavedProfile() {
        // Arrange
        Profile profile = new Profile(null, "newuser", "newuser@example.com", null, "private", 0.0, "USER");
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        Profile savedProfile = profileService.saveProfile(profile);

        // Assert
        assertNotNull(savedProfile);
        assertEquals("newuser", savedProfile.getUsername());
        verify(profileRepository).save(profile);
    }

    @Test
    void saveProfile_Failure_InvalidEmail_ReturnsNull() {
        // Arrange
        Profile profile = new Profile(null, "newuser", "invalid-email", null, "private", 0.0, "USER");

        // Act
        Profile savedProfile = profileService.saveProfile(profile);

        // Assert
        assertNull(savedProfile);
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void updateProfile_Success_ReturnsUpdatedProfile() {
        // Arrange
        Long profileId = 1L;
        Profile existingProfile = new Profile(profileId, "user1", "user1@example.com", null, "private", 0.0, "USER");
        Profile updatedProfile = new Profile(null, "updatedUser", "updated@example.com", "Updated bio", "public", 0.0, "USER");
        
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(updatedProfile);

        // Act
        Optional<Profile> result = profileService.updateProfile(profileId, updatedProfile);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("updatedUser", result.get().getUsername());
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void updateProfile_NotFound_ReturnsEmptyOptional() {
        // Arrange
        Long profileId = 1L;
        Profile updatedProfile = new Profile(null, "updatedUser", "updated@example.com", null, "public", 0.0, "USER");

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act
        Optional<Profile> result = profileService.updateProfile(profileId, updatedProfile);

        // Assert
        assertFalse(result.isPresent());
        verify(profileRepository).findById(profileId);
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void deleteProfile_Success() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(true);

        // Act
        profileService.deleteProfile(profileId);

        // Assert
        verify(profileRepository).deleteById(profileId);
    }

    @Test
    void deleteProfile_NotFound_ThrowsException() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(profileId));
        assertEquals("Profile with id " + profileId + " does not exist", exception.getMessage());
    }

    @Test
    void updateRating_Success() {
        // Arrange
        Long profileId = 1L;
        Profile profile = new Profile(profileId, "user1", "user1@example.com", null, "private", 4.0, "USER");
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        profileService.updateRating(profileId, 5.0);

        // Assert
        assertEquals(5.0, profile.getRating());
        verify(profileRepository).save(profile);
    }

    @Test
    void updateRating_ProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act
        profileService.updateRating(profileId, 5.0);

        // Assert
        verify(profileRepository, never()).save(any(Profile.class));
    }
}
