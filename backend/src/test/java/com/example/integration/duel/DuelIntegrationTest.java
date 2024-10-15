package com.example.integration.duel;

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
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test") // Disable WebSocket

class DuelIntegrationTest {

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

    @AfterEach
    void tearDown() {
        duelRepository.deleteAll();
        profileRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        Long pid1 = 2L;
        Long pid2 = 3L;

        tournament = tournamentRepository.save(new Tournament(
                "Test Tournament",
                true,
                Date.valueOf("2023-12-25"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Test"));

        DuelResult result = new DuelResult(3L, 4L);
        duel = new Duel(null, pid1, pid2, "Round 1", result, result.getWinnerId(), tournament);

        duelRepository.save(duel);

        token = "";

        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void getDuelsByTournament_ValidTournamentId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByTournament_InvalidTournamentId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel?tid=1");

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void getDuelById_ValidId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        ResponseEntity<Duel> result = restTemplate.getForEntity(uri, Duel.class);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelById_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/999"); // Assuming 999 is a non-existent ID

        ResponseEntity<Duel> result = restTemplate.getForEntity(uri, Duel.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByRoundName_ValidRoundName_Success() throws Exception {
        String roundName = URLEncoder.encode("Round 1", StandardCharsets.UTF_8.toString());
        URI uri = new URI(baseUrl + port + "/api/duel/round?roundName=" + roundName);

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Duel>>() {
                });
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByRoundName_NoRoundName_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/round");

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByPlayer_ValidPlayerId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/player?pid=" +
                duel.getPid1());

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByPlayer_NoPlayerId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/player");

        ResponseEntity<List<Duel>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Duel>>() {
                });

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void createDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");

        HttpEntity<Duel> requestEntity = new HttpEntity<>(duel, headers);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Duel.class);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void createDuel_SamePlayer_Failure() throws Exception {
        Long player = 4L;

        // Construct a Duel object where both players are the same
        Duel duel = new Duel();
        duel.setPid1(player);
        duel.setPid2(player); // Same player for both fields
        duel.setRoundName("Round 1");
        duel.setWinner(1L);
        duel.setTournament(tournament);

        URI uri = new URI(baseUrl + port + "/api/duel");

        HttpEntity<Duel> requestEntity = new HttpEntity<>(duel, headers);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Duel.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void createDuel_AlreadyExist_Failure() throws Exception {
        // Simulating the duel that already exists in the database
        Long pid1 = 2L;
        Long pid2 = 3L;

        Duel duel = new Duel();
        duel.setPid1(pid1);
        duel.setPid2(pid2);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);
        duel.setTournament(tournament);

        URI uri = new URI(baseUrl + port + "/api/duel");

        HttpEntity<Duel> requestEntity = new HttpEntity<>(duel, headers);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Duel.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void updateDuelResult_Success() throws Exception {
        duel = duelRepository.save(duel);
        entityManager.flush(); // Ensures that the duel is committed to the DB

        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId() + "/result");

        DuelResult newResult = new DuelResult(7L, 8L); 

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newResult, headers),
                Duel.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        Duel updatedDuel = result.getBody();
        assertEquals(newResult.getWinnerId(), updatedDuel.getResult().getWinnerId());
        assertEquals(newResult.getLoserId(), updatedDuel.getResult().getLoserId());
    }

    @Test
    public void updateDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());
        Duel updatedDuel = new Duel(null, 4L, 5L, "Round 2", new DuelResult(1L, 2L), 1L, tournament);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(updatedDuel),
                Duel.class);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void deleteDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}