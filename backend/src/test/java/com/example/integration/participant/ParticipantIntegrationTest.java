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
import com.example.integration.utils.CognitoAuthUtils;

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
        String username = "khairyo";
        String password = "Hello12.";
        this.jwtToken = CognitoAuthUtils.getJwtToken(username, password);
    }

    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    public void testRegisterParticipant_Success() throws Exception {
        Participant participant = new Participant();
        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L);
        participant.setTournament(tournament);

        Profile profile = new Profile();
        profile.setProfileId(1L);
        participant.setProfile(profile);

        participant.setWin(3);
        participant.setLose(1);

        URI uri = new URI("http://localhost:" + port + "/participants/register");
        HttpEntity<Participant> entity = new HttpEntity<>(participant, createHeadersWithAuth());

        ResponseEntity<Participant> response = restTemplate.postForEntity(uri, entity, Participant.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(participant.getWin(), response.getBody().getWin());
        assertEquals(participant.getLose(), response.getBody().getLose());
    }

    @Test
    public void testRegisterParticipant_Failure_BadRequest() throws Exception {
        Participant participant = new Participant();
        participant.setWin(3);
        participant.setLose(1);

        URI uri = new URI("http://localhost:" + port + "/participants/register");
        HttpEntity<Participant> entity = new HttpEntity<>(participant, createHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRegisterParticipant_Failure_Conflict() throws Exception {
        testRegisterParticipant_Success();

        Participant participant = new Participant();
        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L);
        participant.setTournament(tournament);

        Profile profile = new Profile();
        profile.setProfileId(1L);
        participant.setProfile(profile);

        participant.setWin(3);
        participant.setLose(1);

        URI uri = new URI("http://localhost:" + port + "/participants/register");
        HttpEntity<Participant> entity = new HttpEntity<>(participant, createHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testGetAllParticipants_Success() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/participants");

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetParticipantsByTournamentId_Success() throws Exception {
        Long tournamentId = 1L;
        URI uri = new URI("http://localhost:" + port + "/participants/tournament/" + tournamentId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetParticipantsByTournamentId_Failure_NotFound() throws Exception {
        Long tournamentId = 999L;
        URI uri = new URI("http://localhost:" + port + "/participants/tournament/" + tournamentId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetParticipantsByUserId_Success() throws Exception {
        Long userId = 1L;
        URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetParticipantsByUserId_Failure_NotFound() throws Exception {
        Long userId = 999L;
        URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteParticipant_Success() throws Exception {
        Long userId = 1L;
        Long tournamentId = 1L;
        URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId + "/tournament/" + tournamentId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<Participant> getResponse = restTemplate.getForEntity(uri, Participant.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void testDeleteParticipant_Failure_NotFound() throws Exception {
        Long userId = 999L;
        Long tournamentId = 999L;
        URI uri = new URI("http://localhost:" + port + "/participants/user/" + userId + "/tournament/" + tournamentId);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}