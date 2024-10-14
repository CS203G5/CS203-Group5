package com.example.integration.duel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import com.example.duel.*;
import com.example.profile.*;
import com.example.tournament.*;

import java.net.URI;
import java.sql.*;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DuelIntegrationTest {

    private Duel duel;
    private Profile player1;
    private Profile player2;

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DuelRepository duelRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TournamentRepository tournamentRepository; // Add this if needed

    @BeforeEach
    void setUp() {
        player1 = profileRepository
                .save(new Profile(1L, "test1", "test1@email.com", "test 1", "public", 0.0, "ROLE_PLAYER"));
        player2 = profileRepository
                .save(new Profile(2L, "test2", "test2@email.com", "test 2", "public", 0.0, "ROLE_PLAYER"));

        Tournament tournament = tournamentRepository.save(new Tournament(
                "Test Tournament",
                true,
                Date.valueOf("2023-12-25"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Test"));

        DuelResult result = new DuelResult(3L, 4L);
        duel = new Duel(null, "Round 1", result, 3L, player1, player2, tournament);

        // Save the duel to the repository
        duelRepository.save(duel);
    }

    @AfterEach
    void tearDown() {
        // Clear the database after each test
        duelRepository.deleteAll();
    }

    @Test
    public void getDuelsByTournament_ValidTournamentId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel?tid=1");

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void getDuelsByTournament_NoTournamentId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void getDuelById_ValidId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        ResponseEntity<Duel> result = restTemplate.getForEntity(uri, Duel.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(duel.getRoundName(), result.getBody().getRoundName());
    }

    @Test
    public void getDuelById_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/999"); // Assuming 999 is invalid

        ResponseEntity<Duel> result = restTemplate.getForEntity(uri, Duel.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void getDuelsByRoundName_ValidRoundName_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/round?roundName=Round 1");

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void getDuelsByRoundName_NoRoundName_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/round");

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void getDuelsByPlayer_ValidPlayerId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/player?pid=" + player1.getProfileId());

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void getDuelsByPlayer_NoPlayerId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/player");

        ResponseEntity<List> result = restTemplate.getForEntity(uri, List.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size()); // Assuming one duel exists
    }

    @Test
    public void createDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel");
        Duel newDuel = new Duel(2L, "Round 2", new DuelResult(5L, 6L), 3L, player1, player2, null);

        ResponseEntity<Duel> result = restTemplate.postForEntity(uri, newDuel, Duel.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(newDuel.getRoundName(), result.getBody().getRoundName());
    }

    @Test
    public void updateDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());
        Duel updatedDuel = new Duel(1L, "Updated Round", duel.getResult(), 3L, player1, player2, duel.getTournament());

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(updatedDuel),
                Duel.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Updated Round", result.getBody().getRoundName());
    }

    @Test
    public void updateDuelResult_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId() + "/result");
        DuelResult newResult = new DuelResult(7L, 8L);

        ResponseEntity<Duel> result = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newResult),
                Duel.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(7L, result.getBody().getResult().getPlayer1Time());
        assertEquals(8L, result.getBody().getResult().getPlayer2Time());
    }

    @Test
    public void deleteDuel_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/duel/" + duel.getDuelId());

        restTemplate.delete(uri);

        ResponseEntity<Duel> result = restTemplate.getForEntity(uri, Duel.class);
        assertEquals(404, result.getStatusCode().value());
    }
}
