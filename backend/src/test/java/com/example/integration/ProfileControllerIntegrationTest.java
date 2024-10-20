package com.example.integration;

import com.example.integration.CognitoAuthUtils;
import com.example.profile.Profile;
import com.example.profile.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProfileRepository profileRepository;

    private String jwtToken;
    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        // Generate the JWT token using CognitoAuthUtils
        String username = "khairyo";  // Replace with valid username
        String password = "Hello12."; // Replace with valid password

        try {
            jwtToken = CognitoAuthUtils.getJwtToken(username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token for test setup", e);
        }

        // Set up the headers with the generated JWT token
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);  // Add JWT token to Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    // Test getProfile by ID
    @Test
    public void testGetProfileById() throws Exception {
        Profile profile = new Profile(null, "testuser", "testuser@example.com", "Test bio", "Public", 0.0, "USER");
        profile = profileRepository.save(profile);

        mockMvc.perform(get("/profile/" + profile.getProfileId())
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test searchProfiles
    @Test
    public void testSearchProfiles() throws Exception {
        Profile profile = new Profile(null, "testuser", "testuser@example.com", "Test bio", "Public", 0.0, "USER");
        profileRepository.save(profile);

        mockMvc.perform(get("/profile/search")
                .param("searchTerm", "test")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test createProfile
    @Test
    public void testCreateProfile() throws Exception {
        String profileJson = "{ \"username\": \"newuser\", \"email\": \"newuser@example.com\", \"bio\": \"New bio\", \"privacy_settings\": \"Public\", \"rating\": 0.0, \"role\": \"USER\" }";

        mockMvc.perform(post("/profile")
                .headers(headers)
                .content(profileJson))
                .andExpect(status().isOk())  // Assuming success returns 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test updateProfile
    @Test
    public void testUpdateProfile() throws Exception {
        Profile profile = new Profile(null, "testuser", "testuser@example.com", "Test bio", "Public", 0.0, "USER");
        profile = profileRepository.save(profile);

        String updatedProfileJson = "{ \"username\": \"updateduser\", \"email\": \"updateduser@example.com\", \"bio\": \"Updated bio\", \"privacy_settings\": \"Private\", \"rating\": 0.0, \"role\": \"USER\" }";

        mockMvc.perform(put("/profile/" + profile.getProfileId())
                .headers(headers)
                .content(updatedProfileJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test deleteProfile
    @Test
    public void testDeleteProfile() throws Exception {
        Profile profile = new Profile(null, "testuser", "testuser@example.com", "Test bio", "Public", 0.0, "USER");
        profile = profileRepository.save(profile);

        mockMvc.perform(delete("/profile/" + profile.getProfileId())
                .headers(headers))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    // Test updateRating (admin-only endpoint)
    @Test
    public void testUpdateRating() throws Exception {
        Profile profile = new Profile(null, "testuser", "testuser@example.com", "Test bio", "Public", 0.0, "ADMIN");
        profile = profileRepository.save(profile);

        mockMvc.perform(put("/profile/" + profile.getProfileId() + "/rating")
                .headers(headers)
                .param("newRating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}
