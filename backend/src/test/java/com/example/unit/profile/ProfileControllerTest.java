package com.example.unit.profile;

import com.example.profile.Profile;
import com.example.profile.ProfileController;
import com.example.profile.ProfileService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Profile testProfile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
        
        testProfile = new Profile();
        testProfile.setProfileId(1L);
        testProfile.setUsername("testuser");
        testProfile.setEmail("testuser@example.com");
        testProfile.setBio("This is a test bio");
        testProfile.setPrivacySettings("PUBLIC");
        testProfile.setRating(5.0);
        testProfile.setRole("USER");
    }

    @Test
    void getProfile_ProfileFound_ReturnsProfile() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(Optional.of(testProfile));

        mockMvc.perform(get("/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
        
        verify(profileService, times(1)).getProfile(1L);
    }

    @Test
    void getProfile_ProfileNotFound_ReturnsNotFound() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile/1"))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).getProfile(1L);
    }

    @Test
    void searchProfiles_ReturnsListOfProfiles() throws Exception {
        when(profileService.searchProfiles("test")).thenReturn(Arrays.asList(testProfile));

        mockMvc.perform(get("/profile/search")
                .param("searchTerm", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));

        verify(profileService, times(1)).searchProfiles("test");
    }

    @Test
    void createProfile_ValidInput_ReturnsCreatedProfile() throws Exception {
        when(profileService.saveProfile(any(Profile.class))).thenReturn(testProfile);

        mockMvc.perform(post("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(profileService, times(1)).saveProfile(any(Profile.class));
    }

    @Test
    void updateProfile_ProfileFound_ReturnsUpdatedProfile() throws Exception {
        when(profileService.updateProfile(eq(1L), any(Profile.class))).thenReturn(Optional.of(testProfile));

        mockMvc.perform(put("/profile/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(profileService, times(1)).updateProfile(eq(1L), any(Profile.class));
    }

    @Test
    void updateProfile_ProfileNotFound_ReturnsNotFound() throws Exception {
        when(profileService.updateProfile(eq(1L), any(Profile.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/profile/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).updateProfile(eq(1L), any(Profile.class));
    }

    @Test
    void deleteProfile_ProfileDeletedSuccessfully_ReturnsNoContent() throws Exception {
        doNothing().when(profileService).deleteProfile(1L);

        mockMvc.perform(delete("/profile/1"))
                .andExpect(status().isNoContent());

        verify(profileService, times(1)).deleteProfile(1L);
    }

    @Test
    void deleteProfile_ProfileDeletionFails_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException()).when(profileService).deleteProfile(1L);

        mockMvc.perform(delete("/profile/1"))
                .andExpect(status().isBadRequest());

        verify(profileService, times(1)).deleteProfile(1L);
    }

    @Test
    void updateRating_AdminUpdatesRating_ReturnsUpdatedProfile() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(Optional.of(testProfile));

        mockMvc.perform(put("/profile/1/rating")
                .param("newRating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5.0));

        verify(profileService, times(1)).updateRating(1L, 4.5);
    }

    @Test
    void updateRating_ProfileNotFound_ReturnsNotFound() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/profile/1/rating")
                .param("newRating", "4.5"))
                .andExpect(status().isNotFound());

        verify(profileService, times(0)).updateRating(anyLong(), anyDouble());
    }

    @Test
    void getProfileByUsername_ProfileFound_ReturnsProfile() throws Exception {
        when(profileService.getProfileByUsername("testuser")).thenReturn(Optional.of(testProfile));

        mockMvc.perform(get("/profile/by-username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(profileService, times(1)).getProfileByUsername("testuser");
    }

    @Test
    void getProfileByUsername_ProfileNotFound_ReturnsNotFound() throws Exception {
        when(profileService.getProfileByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile/by-username/testuser"))
                .andExpect(status().isNotFound());

        verify(profileService, times(1)).getProfileByUsername("testuser");
    }
}