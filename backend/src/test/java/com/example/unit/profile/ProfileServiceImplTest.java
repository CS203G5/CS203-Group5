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
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfile_Success() {
        Profile profile = new Profile(1L, "username", "email@example.com", "bio", "private", 5.0, "USER");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Optional<Profile> result = profileService.getProfile(1L);
        assertTrue(result.isPresent());
        assertEquals("username", result.get().getUsername());
    }

    @Test
    void testGetProfile_NotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Profile> result = profileService.getProfile(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSearchProfiles() {
        List<Profile> profiles = Arrays.asList(new Profile(1L, "user1", "email@example.com", "bio", "private", 4.5, "USER"));
        when(profileRepository.findByUsernameContainingIgnoreCase("user")).thenReturn(profiles);

        List<Profile> result = profileService.searchProfiles("user");
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void testSaveProfile_Success() {
        Profile profile = new Profile(1L, "username", "email@example.com", "bio", "private", 5.0, "USER");
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.saveProfile(profile);
        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    void testSaveProfile_InvalidUsername() {
        Profile profile = new Profile(1L, null, "email@example.com", "bio", "private", 5.0, "USER");

        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_InvalidEmail() {
        Profile profile = new Profile(1L, "username", "invalid-email", "bio", "private", 5.0, "USER");

        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_InvalidUsernameAndEmail() {
        Profile profile = new Profile(1L, null, "invalid-email", "bio", "private", 5.0, "USER");
        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }


    @Test
    void testUpdateProfile_Success() {
        Profile existingProfile = new Profile(1L, "oldUsername", "old@example.com", "oldBio", "private", 4.0, "USER");
        Profile updatedProfile = new Profile(1L, "newUsername", "new@example.com", "newBio", "public", 5.0, "USER");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(updatedProfile);

        Optional<Profile> result = profileService.updateProfile(1L, updatedProfile);
        assertTrue(result.isPresent());
        assertEquals("newUsername", result.get().getUsername());
        assertEquals("new@example.com", result.get().getEmail());
    }

    @Test
    void testUpdateProfile_NotFound() {
        Profile updatedProfile = new Profile(1L, "newUsername", "new@example.com", "newBio", "public", 5.0, "USER");
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Profile> result = profileService.updateProfile(1L, updatedProfile);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteProfile_Success() {
        when(profileRepository.existsById(1L)).thenReturn(true);
        doNothing().when(profileRepository).deleteById(1L);

        profileService.deleteProfile(1L);
        verify(profileRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProfile_NotFound() {
        when(profileRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            profileService.deleteProfile(1L);
        });
        assertEquals("Profile with id 1 does not exist", exception.getMessage());
    }

    @Test
    void testUpdateRating_Success() {
        Profile profile = new Profile(1L, "username", "email@example.com", "bio", "private", 3.0, "USER");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        profileService.updateRating(1L, 4.5);
        assertEquals(4.5, profile.getRating());
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testUpdateRating_ProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        profileService.updateRating(1L, 4.5);
        verify(profileRepository, never()).save(any());
    }

    @Test
    void testGetProfileByUsername_Success() {
        Profile profile = new Profile(1L, "username", "email@example.com", "bio", "private", 5.0, "USER");
        when(profileRepository.findByUsername("username")).thenReturn(Optional.of(profile));

        Optional<Profile> result = profileService.getProfileByUsername("username");
        assertTrue(result.isPresent());
        assertEquals("username", result.get().getUsername());
    }

    @Test
    void testGetProfileByUsername_NotFound() {
        when(profileRepository.findByUsername("username")).thenReturn(Optional.empty());

        Optional<Profile> result = profileService.getProfileByUsername("username");
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteProfile_ProfileNotFound() {
        when(profileRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            profileService.deleteProfile(1L);
        });

        assertEquals("Profile with id 1 does not exist", thrown.getMessage());
        verify(profileRepository, never()).deleteById(1L);
    }

    @Test
    void testSaveProfile_EmptyUsername() {
        Profile profile = new Profile(1L, "", "email@example.com", "bio", "private", 5.0, "USER");
        
        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_WhitespaceUsername() {
        Profile profile = new Profile(1L, "   ", "email@example.com", "bio", "private", 5.0, "USER");
        
        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_NullEmail() {
        Profile profile = new Profile(1L, "username", null, "bio", "private", 5.0, "USER");
        
        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_EmptyEmail() {
        Profile profile = new Profile(1L, "username", "", "bio", "private", 5.0, "USER");
        
        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_InvalidEmailFormat_NoAtSymbol() {
        Profile profile = new Profile(1L, "username", "invalidemail.com", "bio", "private", 5.0, "USER");

        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testSaveProfile_InvalidEmailFormat_InvalidDomain() {
        Profile profile = new Profile(1L, "username", "email@.com", "bio", "private", 5.0, "USER");

        Profile result = profileService.saveProfile(profile);
        assertNull(result);
    }

    @Test
    void testUpdateRating_ProfileNotFound_NoUpdate() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        profileService.updateRating(1L, 4.5);

        // Verify no save operation took place
        verify(profileRepository, never()).save(any(Profile.class));
    }

}
