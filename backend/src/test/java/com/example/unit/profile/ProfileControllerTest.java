package com.example.unit.profile;

import com.example.profile.Profile;
import com.example.profile.ProfileController;
import com.example.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private ProfileService profileService;

    private Profile profile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profile = new Profile(1L, "testuser", "test@example.com", "Test Bio", "PUBLIC", 4.5);
    }

    // Success case for getting a profile by ID
    @Test
    void testGetProfile_Success() {
        when(profileService.getProfile(1L)).thenReturn(Optional.of(profile));

        ResponseEntity<Profile> response = profileController.getProfile(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    // Failure case for getting a profile by ID
    @Test
    void testGetProfile_Failure_NotFound() {
        when(profileService.getProfile(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Profile> response = profileController.getProfile(9999L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    // Success case for searching profiles
    @Test
    void testSearchProfiles_Success() {
        List<Profile> profiles = Arrays.asList(profile);
        when(profileService.searchProfiles("test")).thenReturn(profiles);

        ResponseEntity<List<Profile>> response = profileController.searchProfiles("test");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
    }

    // Success case for creating a profile
    @Test
    void testCreateProfile_Success() {
        when(profileService.saveProfile(any(Profile.class))).thenReturn(profile);

        ResponseEntity<Profile> response = profileController.createProfile(profile);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    // Success case for updating a profile
    @Test
    void testUpdateProfile_Success() {
        when(profileService.updateProfile(anyLong(), any(Profile.class))).thenReturn(profile);

        ResponseEntity<Profile> response = profileController.updateProfile(1L, profile);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    // Failure case for updating a profile when profile is not found
    @Test
    void testUpdateProfile_Failure_NotFound() {
        when(profileService.updateProfile(anyLong(), any(Profile.class))).thenReturn(null);

        ResponseEntity<Profile> response = profileController.updateProfile(9999L, profile);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    // Success case for deleting a profile
    @Test
    void testDeleteProfile_Success() {
        doNothing().when(profileService).deleteProfile(anyLong());

        ResponseEntity<Void> response = profileController.deleteProfile(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(profileService, times(1)).deleteProfile(1L);
    }

    // Failure case for deleting a profile when profile is not found
    @Test
    void testDeleteProfile_Failure_NotFound() {
        doThrow(new IllegalArgumentException("Profile not found")).when(profileService).deleteProfile(anyLong());

        ResponseEntity<Void> response = profileController.deleteProfile(9999L);

        assertEquals(400, response.getStatusCodeValue());
    }

    // Success case for updating a profile rating
    @Test
    void testUpdateRating_Success() {
        when(profileService.getProfile(1L)).thenReturn(Optional.of(profile));
        doNothing().when(profileService).updateRating(1L, 4.8);

        ResponseEntity<Profile> response = profileController.updateRating(1L, 4.8);

        assertEquals(200, response.getStatusCodeValue());
        verify(profileService, times(1)).updateRating(1L, 4.8);
    }

    // Failure case for updating a profile rating when profile is not found
    @Test
    void testUpdateRating_Failure_NotFound() {
        when(profileService.getProfile(9999L)).thenReturn(Optional.empty());

        ResponseEntity<Profile> response = profileController.updateRating(9999L, 4.8);

        assertEquals(404, response.getStatusCodeValue());
        verify(profileService, never()).updateRating(anyLong(), anyDouble());
    }
}
