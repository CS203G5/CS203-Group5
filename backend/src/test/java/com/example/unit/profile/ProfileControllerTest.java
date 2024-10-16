package com.example.unit.profile;

import com.example.profile.Profile;
import com.example.profile.ProfileController;
import com.example.profile.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;

    private Profile profile;

    @BeforeEach
    public void setUp() {
        profile = new Profile(1L, "john_doe", "john@example.com", "Hello, I'm John", "PRIVATE", 4.5);
    }

    // Success Case: Get Profile by ID
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testGetProfileSuccess() throws Exception {
        Mockito.when(profileService.getProfile(1L)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/profile/{profileId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    // Failure Case: Get Profile - Not Found
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testGetProfileNotFound() throws Exception {
        Mockito.when(profileService.getProfile(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile/{profileId}", 1L))
                .andExpect(status().isNotFound());
    }

    // Success Case: Create Profile
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testCreateProfileSuccess() throws Exception {
        Mockito.when(profileService.saveProfile(any(Profile.class))).thenReturn(profile);

        mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    // Success Case: Search Profiles
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testSearchProfilesSuccess() throws Exception {
        Mockito.when(profileService.searchProfiles("john"))
                .thenReturn(Arrays.asList(profile));

        mockMvc.perform(get("/profile/search")
                        .param("searchTerm", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john_doe"));
    }

    // Success Case: Update Profile
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testUpdateProfileSuccess() throws Exception {
        Mockito.when(profileService.updateProfile(anyLong(), any(Profile.class)))
                .thenReturn(profile);

        mockMvc.perform(put("/profile/{profileId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    // Failure Case: Update Profile - Not Found
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testUpdateProfileNotFound() throws Exception {
        Mockito.when(profileService.updateProfile(anyLong(), any(Profile.class)))
                .thenReturn(null);

        mockMvc.perform(put("/profile/{profileId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isNotFound());
    }

    // Success Case: Delete Profile
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testDeleteProfileSuccess() throws Exception {
        Mockito.doNothing().when(profileService).deleteProfile(1L);

        mockMvc.perform(delete("/profile/{profileId}", 1L))
                .andExpect(status().isNoContent());
    }

    // Failure Case: Delete Profile - Bad Request
    @Test
    @WithMockUser // Simulate an authenticated user
    public void testDeleteProfileBadRequest() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Profile not found"))
                .when(profileService).deleteProfile(anyLong());

        mockMvc.perform(delete("/profile/{profileId}", 1L))
                .andExpect(status().isBadRequest());
    }

    // Success Case: Update Rating - Admin Only
    @Test
    @WithMockUser(roles = "ADMIN") // Simulate an admin user
    public void testUpdateRatingSuccess() throws Exception {
        Mockito.when(profileService.getProfile(1L)).thenReturn(Optional.of(profile));

        mockMvc.perform(put("/profile/{profileId}/rating", 1L)
                        .param("newRating", "4.7"))
                .andExpect(status().isOk());
    }
}
