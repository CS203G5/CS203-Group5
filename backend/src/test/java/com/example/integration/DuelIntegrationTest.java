package com.example.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.duel.*;
import com.example.profile.*;
import com.example.tournament.*;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class DuelIntegrationTest {

    private Profile player1;
    private Profile player2;
    private Tournament tournament;
    private Duel duel;
    private String token;
    private HttpHeaders headers;

    @LocalServerPort
    private int port;
    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DuelRepository duelRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @BeforeEach
    void setUp() {
        // Create unique players and tournament data
        player1 = new Profile(null, "test1", "test1@email.com", "Player One", "public", 0.0, "ROLE_PLAYER");
        player2 = new Profile(null, "test2", "test2@email.com", "Player Two", "public", 0.0, "ROLE_PLAYER");

        player1 = profileRepository.save(player1);
        player2 = profileRepository.save(player2);

        tournament = tournamentRepository.save(new Tournament(
                "Test Tournament 1",
                true,
                Date.valueOf("2023-12-26"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Organizer"
        ));

        DuelResult result = new DuelResult(3000L, 4000L);
        duel = new Duel();
        duel.setPid1(player1);
        duel.setPid2(player2);
        duel.setRoundName("Round 1");
        duel.setResult(result);
        duel.setTournament(tournament);
        duelRepository.save(duel);

        // Generate the JWT token using CognitoAuthUtils
        String username = "khairyo";  // Replace with valid username
        String password = "Hello12."; // Replace with valid password
        token = CognitoAuthUtils.getJwtToken(username, password);

        // Set up the headers with the generated JWT token
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Add JWT token to Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void getDuelsByTournament_ValidTournamentId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByTournament_InvalidTournamentId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel?tid=9999"); // Non-existent tournament ID

        ResponseEntity<String> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void getDuelById_ValidId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        ResponseEntity<Duel> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                Duel.class);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelById_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/999"); // Assuming 999 is a non-existent ID

        ResponseEntity<Duel> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                Duel.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByRoundName_ValidRoundName_Success() throws Exception {
        String roundName = URLEncoder.encode("Round 1", StandardCharsets.UTF_8.toString());
        URI uri = new URI(baseUrl + port + "/api/duel/round?roundName=" + roundName);

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                new ParameterizedTypeReference<List<Duel>>() {
                });
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByPlayer_ValidPlayerId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/player?pid=" +
                player1.getProfileId());

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Include headers with JWT token
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void createDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");

        Duel newDuel = new Duel();
        newDuel.setPid1(player1);
        newDuel.setPid2(player2);
        newDuel.setRoundName("Round 2");
        newDuel.setTournament(tournament);
        newDuel.setResult(new DuelResult(2000L, 3000L));

        HttpEntity<Duel> requestEntity = new HttpEntity<>(newDuel, headers); // Include headers with JWT token

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Duel.class);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void createDuel_SamePlayer_Failure() throws Exception {
        // Construct a Duel object where both players are the same
        Duel duel = new Duel();
        duel.setPid1(player1);
        duel.setPid2(player2); // Same player for both fields
        duel.setRoundName("Round 1");
        duel.setWinner(player1.getProfileId());
        duel.setTournament(tournament);

        URI uri = new URI(baseUrl + port + "/api/duel");

        HttpEntity<Duel> requestEntity = new HttpEntity<>(duel, headers); // Include headers with JWT token

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Duel.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void updateDuelResult_Success() throws Exception {
        duel = duelRepository.save(duel);
        entityManager.flush(); // Ensures that the duel is committed to the DB

        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId() + "/result");

        DuelResult newResult = new DuelResult(5000L, 6000L);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newResult, headers),
                Duel.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        Duel updatedDuel = result.getBody();
        assertEquals(newResult.getPlayer1Time(), updatedDuel.getResult().getPlayer1Time());
        assertEquals(newResult.getPlayer2Time(), updatedDuel.getResult().getPlayer2Time());
    }

    @Test
    public void deleteDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
