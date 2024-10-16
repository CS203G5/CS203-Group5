package com.example.unit.profile;

import com.example.profile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private ProfileRepository profileRepository;

    private Profile profile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profile = new Profile(1L, "testuser", "test@example.com", "Test Bio", "PUBLIC", 4.5, "USER");
    }

    // Success case for getting a profile by ID
    @Test
    void testGetProfile_Success() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Optional<Profile> result = profileService.getProfile(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    // Failure case for getting a profile by ID
    @Test
    void testGetProfile_Failure_NotFound() {
        when(profileRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<Profile> result = profileService.getProfile(9999L);

        assertFalse(result.isPresent());
    }

    // Success case for searching profiles
    @Test
    void testSearchProfiles_Success() {
        List<Profile> profiles = Arrays.asList(profile);
        when(profileRepository.findByUsernameContainingIgnoreCase("test")).thenReturn(profiles);

        List<Profile> result = profileService.searchProfiles("test");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    // Success case for saving a profile
    @Test
    void testSaveProfile_Success() {
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.saveProfile(profile);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    // Success case for updating a profile
    @Test
    void testUpdateProfile_Success() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile updatedProfile = new Profile(1L, "updateduser", "updated@example.com", "Updated Bio", "PRIVATE", 4.0, "ADMIN");
        Profile result = profileService.updateProfile(1L, updatedProfile);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
    }

    // Failure case for updating a profile when profile is not found
    @Test
    void testUpdateProfile_Failure_NotFound() {
        when(profileRepository.findById(9999L)).thenReturn(Optional.empty());

        Profile result = profileService.updateProfile(9999L, profile);

        assertNull(result);
    }

    // Success case for deleting a profile
    @Test
    void testDeleteProfile_Success() {
        when(profileRepository.existsById(1L)).thenReturn(true);
        doNothing().when(profileRepository).deleteById(1L);

        profileService.deleteProfile(1L);

        verify(profileRepository, times(1)).deleteById(1L);
    }

    // Failure case for deleting a profile when profile is not found
    @Test
    void testDeleteProfile_Failure_NotFound() {
        when(profileRepository.existsById(9999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(9999L));
        verify(profileRepository, never()).deleteById(9999L);
    }

    // Success case for updating a profile rating
    @Test
    void testUpdateRating_Success() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.updateRating(1L, 4.8);

        assertEquals(4.8, profile.getRating());
        verify(profileRepository, times(1)).save(profile);
    }

    // Failure case for updating a profile rating when profile is not found
    @Test
    void testUpdateRating_Failure_NotFound() {
        when(profileRepository.findById(9999L)).thenReturn(Optional.empty());

        profileService.updateRating(9999L, 4.8);

        verify(profileRepository, never()).save(any(Profile.class));
    }
}