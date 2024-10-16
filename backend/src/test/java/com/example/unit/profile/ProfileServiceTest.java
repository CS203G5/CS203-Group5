package com.example.unit.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows; 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never; 
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.profile.Profile;
import com.example.profile.ProfileRepository;
import com.example.profile.ProfileService;
import com.example.profile.ProfileServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void saveProfile_NewProfile_ReturnSavedProfile() {
        // Arrange
        Profile profile = new Profile(null, "NewUser", "newuser@example.com", "Bio", "Public", 0.0);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        Profile savedProfile = profileService.saveProfile(profile);

        // Assert
        assertNotNull(savedProfile);
        verify(profileRepository).save(profile);
    }

    @Test
    public void saveProfile_InvalidProfile_ReturnNull() {
        // Arrange
        Profile profile = new Profile(null, "", "invalidEmail", "Bio", "Public", 0.0); // Invalid profile with empty username
    
        // Act
        Profile savedProfile = profileService.saveProfile(profile);
    
        // Assert
        assertNull(savedProfile); 
        verify(profileRepository, never()).save(profile); 
    }
    

    @Test
    void updateProfile_NotFound_ReturnNull() {
        // Arrange
        Profile updatedProfile = new Profile(null, "UpdatedUser", "updated@example.com", "Updated Bio", "Private", 0.0);
        Long profileId = 10L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act
        Profile result = profileService.updateProfile(profileId, updatedProfile);

        // Assert
        assertNull(result);
        verify(profileRepository).findById(profileId);
    }

    @Test
    void getProfile_Found_ReturnsProfile() {
        // Arrange
        Long profileId = 1L;
        Profile profile = new Profile(profileId, "User1", "user1@example.com", "Bio", "Public", 0.0);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        Optional<Profile> result = profileService.getProfile(profileId);

        // Assert
        assertNotNull(result);
        assertEquals(profile, result.get());
        verify(profileRepository).findById(profileId);
    }

    @Test
    void getProfile_NotFound_ReturnsEmpty() {
        // Arrange
        Long profileId = 2L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act
        Optional<Profile> result = profileService.getProfile(profileId);

        // Assert
        assertNull(result.orElse(null));
        verify(profileRepository).findById(profileId);
    }

    @Test
    void deleteProfile_Exists_DeletesProfile() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(true);

        // Act
        profileService.deleteProfile(profileId);

        // Assert
        verify(profileRepository).deleteById(profileId);
    }

    @Test
    void deleteProfile_NotExists_ThrowsException() {
        // Arrange
        Long profileId = 2L;
        when(profileRepository.existsById(profileId)).thenReturn(false);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(profileId));
        assertEquals("Profile with id " + profileId + " does not exist", exception.getMessage());
        verify(profileRepository, never()).deleteById(profileId);
    }
}
