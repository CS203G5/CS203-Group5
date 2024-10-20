package com.example.unit.profile;

import com.example.profile.Profile;
import com.example.profile.ProfileRepository;
import com.example.profile.ProfileServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private Profile profile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        profile = new Profile(1L, "john_doe", "john@example.com", "Hello, I'm John", "PRIVATE", 4.5);
    }

    // Success Case: Get Profile
    @Test
    public void testGetProfileSuccess() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Optional<Profile> foundProfile = profileService.getProfile(1L);
        assertTrue(foundProfile.isPresent());
        assertEquals("john_doe", foundProfile.get().getUsername());
    }

    // Failure Case: Get Profile - Not Found
    @Test
    public void testGetProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Profile> foundProfile = profileService.getProfile(1L);
        assertFalse(foundProfile.isPresent());
    }

    // Success Case: Save Profile
    @Test
    public void testSaveProfileSuccess() {
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile savedProfile = profileService.saveProfile(profile);
        assertEquals("john_doe", savedProfile.getUsername());
    }

    // Success Case: Search Profiles
    @Test
    public void testSearchProfilesSuccess() {
        when(profileRepository.findByUsernameContainingIgnoreCase("john"))
                .thenReturn(Arrays.asList(profile));

        List<Profile> profiles = profileService.searchProfiles("john");
        assertEquals(1, profiles.size());
        assertEquals("john_doe", profiles.get(0).getUsername());
    }

    // Success Case: Update Profile
    @Test
    public void testUpdateProfileSuccess() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile updatedProfile = profileService.updateProfile(1L, profile);
        assertEquals("john_doe", updatedProfile.getUsername());
    }

    // Failure Case: Update Profile - Not Found
    @Test
    public void testUpdateProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        Profile updatedProfile = profileService.updateProfile(1L, profile);
        assertNull(updatedProfile);
    }

    // Success Case: Delete Profile
    @Test
    public void testDeleteProfileSuccess() {
        when(profileRepository.existsById(1L)).thenReturn(true);
        doNothing().when(profileRepository).deleteById(1L);

        assertDoesNotThrow(() -> profileService.deleteProfile(1L));
        verify(profileRepository, times(1)).deleteById(1L);
    }

    // Failure Case: Delete Profile - Not Found
    @Test
    public void testDeleteProfileNotFound() {
        when(profileRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(1L));
    }

    // Success Case: Update Rating
    @Test
    public void testUpdateRatingSuccess() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.updateRating(1L, 4.8);
        assertEquals(4.8, profile.getRating());
        verify(profileRepository, times(1)).save(profile);
    }
}