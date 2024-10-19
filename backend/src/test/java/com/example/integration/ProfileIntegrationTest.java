package com.example.integration;

import com.example.profile.Profile;
import com.example.integration.CognitoAuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import java.net.URI;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        // Generate a valid JWT token for authentication
        String username = "khairyo";
        String password = "Hello12.";
        jwtToken = CognitoAuthUtils.getJwtToken(username, password);

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // Add the JWT token to the Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private String createBaseUrl(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }

    // Success Test Cases

    @Test
    public void testCreateProfile_Success() throws Exception {
        URI uri = new URI(createBaseUrl("/profile"));

        Profile newProfile = new Profile(null, "newuser", "newuser@email.com", "New Bio", "public", 0.0, "ROLE_PLAYER");
        HttpEntity<Profile> entity = new HttpEntity<>(newProfile, headers);

        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Profile.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
    }

    @Test
    public void testGetProfileById_Success() throws Exception {
        // Assuming a profile with id 1 exists
        Long profileId = 1L;
        URI uri = new URI(createBaseUrl("/profile/" + profileId));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Profile.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profileId, response.getBody().getProfileId());
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        // Assuming a profile with id 1 exists
        Long profileId = 1L;
        URI uri = new URI(createBaseUrl("/profile/" + profileId));

        Profile updatedProfile = new Profile(null, "updateduser", "updateduser@email.com", "Updated Bio", "private", 0.0, "ROLE_PLAYER");
        HttpEntity<Profile> entity = new HttpEntity<>(updatedProfile, headers);

        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Profile.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
    }

    @Test
    public void testDeleteProfile_Success() throws Exception {
        // Assuming a profile with id 1 exists
        Long profileId = 1L;
        URI uri = new URI(createBaseUrl("/profile/" + profileId));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testSearchProfiles_Success() throws Exception {
        URI uri = new URI(createBaseUrl("/profile/search?searchTerm=newuser"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateRating_Success() throws Exception {
        // Assuming a profile with id 1 exists
        Long profileId = 1L;
        URI uri = new URI(createBaseUrl("/profile/" + profileId + "/rating?newRating=4.5"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Profile.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4.5, response.getBody().getRating());
    }

    // Failure Test Cases

    @Test
    public void testGetProfileById_Failure_NotFound() throws Exception {
        Long nonExistentProfileId = 9999L;  // Use a non-existent profile ID
        URI uri = new URI(createBaseUrl("/profile/" + nonExistentProfileId));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Profile.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateProfile_Failure_InvalidData() throws Exception {
        URI uri = new URI(createBaseUrl("/profile"));

        Profile invalidProfile = new Profile(null, "", "invalidemail", "Invalid Bio", "public", 0.0, "ROLE_PLAYER");
        HttpEntity<Profile> entity = new HttpEntity<>(invalidProfile, headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateProfile_Failure_NotFound() throws Exception {
        Long nonExistentProfileId = 9999L;  // Use a non-existent profile ID
        URI uri = new URI(createBaseUrl("/profile/" + nonExistentProfileId));

        Profile updatedProfile = new Profile(null, "updateduser", "updateduser@email.com", "Updated Bio", "private", 0.0, "ROLE_PLAYER");
        HttpEntity<Profile> entity = new HttpEntity<>(updatedProfile, headers);

        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Profile.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteProfile_Failure_NotFound() throws Exception {
        Long nonExistentProfileId = 9999L;  // Use a non-existent profile ID
        URI uri = new URI(createBaseUrl("/profile/" + nonExistentProfileId));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateRating_Failure_NotFound() throws Exception {
        Long nonExistentProfileId = 9999L;  // Use a non-existent profile ID
        URI uri = new URI(createBaseUrl("/profile/" + nonExistentProfileId + "/rating?newRating=4.5"));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Profile> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Profile.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
