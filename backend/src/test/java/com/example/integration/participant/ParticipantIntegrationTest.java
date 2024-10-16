package com.example.integration.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.tournament.Tournament;
import com.example.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import com.example.integration.utils.CognitoAuthUtils; // Adjust the package path as necessary

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParticipantIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    @BeforeEach
    public void setUp() {
        // Simulate the CognitoAuthUtils.getJwtToken() to generate a valid JWT token for authorization
        String username = "khairyo";  // use valid username
        String password = "Hello12."; // use valid password
        this.jwtToken = CognitoAuthUtils.getJwtToken(username, password);
    }

    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // Add the JWT token to the Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // @Test
    // public void testRegisterParticipant_Success() throws Exception {
    //     // Create a new Participant object
    //     Participant participant = new Participant();
    //     Tournament tournament = new Tournament();
    //     tournament.setTournamentId(1L);  // Example tournament ID
    //     participant.setTournament(tournament);

    //     Profile profile = new Profile();
    //     profile.setProfileId(1L);  // Example profile ID
    //     participant.setProfile(profile);

    //     participant.setWin(3);
    //     participant.setLose(1);

    //     // Call the API to register a new participant
    //     URI uri = new URI("http://localhost:" + port + "/participants/register");
    //     HttpEntity<Participant> entity = new HttpEntity<>(participant, createHeadersWithAuth());

    //     ResponseEntity<Participant> response = restTemplate.postForEntity(uri, entity, Participant.class);

    //     // Assert the response status and participant properties
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //     assertNotNull(response.getBody());
    //     assertEquals(participant.getWin(), response.getBody().getWin());
    //     assertEquals(participant.getLose(), response.getBody().getLose());
    // }

    @Test
    public void testGetAllParticipants_Success() throws Exception {
        // Test retrieving all participants
        URI uri = new URI("http://localhost:" + port + "/participants");

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        // Assert the response status and list content
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetParticipantsByTournamentId_Success() throws Exception {
        // Test retrieving participants by tournament ID
        Long tournamentId = 1L;  // Example tournament ID
        URI uri = new URI("http://localhost:" + port + "/participants/tournament/" + tournamentId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        // Assert the response status and participants list content
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetParticipantsByUserId_Success() throws Exception {
        // Test retrieving participants by user ID
        Long userId = 1L;  // Example user ID
        URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        // Assert the response status and participants list content
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

//     @Test
//     public void testDeleteParticipant_Success() throws Exception {
//         // Test deleting a participant by user ID and tournament ID
//         Long userId = 1L;  // Example user ID
//         Long tournamentId = 1L;  // Example tournament ID
//         URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId + "/tournament/" + tournamentId);

//         HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

//         // Send a DELETE request to remove the participant
//         ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Void.class);

//         // Assert that the participant was deleted
//         assertEquals(HttpStatus.OK, response.getStatusCode());

//         // Verify the participant is no longer found
//         ResponseEntity<Participant> getResponse = restTemplate.getForEntity(uri, Participant.class);
//         assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
//     }
}