package com.example.integration.tournament;

import com.example.Main;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
@Transactional
public class TournamentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl = "/api/tournaments";

    @Test
    public void testAddTournament() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setName("Integration Test Tournament");
        tournament.setIsRandom(true);
        tournament.setDate(Date.valueOf(LocalDate.now()));
        tournament.setLocation("Test Location");
        tournament.setOrganizer(123L);
        tournament.setDescription("An integration test tournament");

        URI uri = new URI(baseUrl);

        ResponseEntity<Tournament> result = restTemplate.postForEntity(uri, tournament, Tournament.class);
        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("Integration Test Tournament", result.getBody().getName());
    }

    @Test
    public void testGetAllTournaments() {
        ResponseEntity<Tournament[]> result = restTemplate.getForEntity(baseUrl, Tournament[].class);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().length > 0);
    }

    @Test
    public void testUpdateTournament() throws Exception {
        // Add a tournament first
        Tournament tournament = new Tournament();
        tournament.setName("Update Test Tournament");
        tournament.setIsRandom(false);
        tournament.setDate(Date.valueOf(LocalDate.now()));
        tournament.setLocation("Test Location");
        tournament.setOrganizer(123L);
        tournament.setDescription("Update test tournament");

        URI uri = new URI(baseUrl);
        ResponseEntity<Tournament> result = restTemplate.postForEntity(uri, tournament, Tournament.class);
        assertEquals(201, result.getStatusCode().value());

        // Update the tournament
        Tournament updatedTournament = result.getBody();
        updatedTournament.setName("Updated Tournament");

        URI updateUri = new URI(baseUrl + "/" + updatedTournament.getTournament_id());
        restTemplate.put(updateUri, updatedTournament);

        // Verify the update
        ResponseEntity<Tournament> updatedResult = restTemplate.getForEntity(updateUri, Tournament.class);
        assertEquals(200, updatedResult.getStatusCode().value());
        assertEquals("Updated Tournament", updatedResult.getBody().getName());
    }

    @Test
    public void testDeleteTournament() throws Exception {
        // Add a tournament first
        Tournament tournament = new Tournament();
        tournament.setName("Delete Test Tournament");
        tournament.setIsRandom(false);
        tournament.setDate(Date.valueOf(LocalDate.now()));
        tournament.setLocation("Test Location");
        tournament.setOrganizer(123L);
        tournament.setDescription("Delete test tournament");

        URI uri = new URI(baseUrl);
        ResponseEntity<Tournament> result = restTemplate.postForEntity(uri, tournament, Tournament.class);
        assertEquals(201, result.getStatusCode().value());

        // Delete the tournament
        Long tournamentId = result.getBody().getTournament_id();
        URI deleteUri = new URI(baseUrl + "/" + tournamentId);
        restTemplate.delete(deleteUri);

        // Verify deletion
        ResponseEntity<Tournament> deletedResult = restTemplate.getForEntity(deleteUri, Tournament.class);
        assertEquals(404, deletedResult.getStatusCode().value());
    }
}
