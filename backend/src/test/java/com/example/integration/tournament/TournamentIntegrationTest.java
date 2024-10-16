package com.example.integration.tournament;

import com.example.tournament.Tournament;
import com.example.integration.utils.CognitoAuthUtils;
import com.example.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TournamentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    private String jwtToken;
    private HttpHeaders headers;
    private Long tournamentId;  // Declare the tournamentId field

    @BeforeEach
    public void setUp() {
        // Create a new tournament and save it to the repository
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setIsRandom(false);
        tournament.setDate(Date.valueOf("2023-12-25"));
        tournament.setTime(Time.valueOf("10:00:00"));
        tournament.setLocation("Test Location");
        tournament.setOrganizer(1L);
        tournament.setDescription("A test tournament");

        // Save to the repository and ensure the ID is set
        Tournament savedTournament = tournamentRepository.save(tournament);
        assertNotNull(savedTournament.getTournamentId());  // Verify it's saved

        // Optionally, store the ID for use in tests
        this.tournamentId = savedTournament.getTournamentId();  // You can use this in the test

        String username = "khairyo";  // Use valid username
        String password = "Hello12."; // Use valid password
        this.jwtToken = CognitoAuthUtils.getJwtToken(username, password);

        // Setup headers for authenticated requests
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testAddTournament_Success() throws Exception {
        // Create a new tournament object
        Tournament tournament = new Tournament();
        tournament.setName("New Tournament");
        tournament.setIsRandom(true);
        tournament.setDate(Date.valueOf("2024-01-15"));
        tournament.setTime(Time.valueOf("14:00:00"));
        tournament.setLocation("Test Location");
        tournament.setOrganizer(1L);
        tournament.setDescription("Test Description");

        URI uri = new URI("http://localhost:" + port + "/tournament");

        HttpEntity<Tournament> entity = new HttpEntity<>(tournament, headers);
        ResponseEntity<Tournament> response = restTemplate.postForEntity(uri, entity, Tournament.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Tournament", response.getBody().getName());
    }

    @Test
    public void testGetTournamentById_Success() throws Exception {
        // Use the tournamentId set in @BeforeEach
        URI uri = new URI("http://localhost:" + port + "/tournament/" + this.tournamentId);
    
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Tournament> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Tournament.class);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(this.tournamentId, response.getBody().getTournamentId());
    }    

    @Test
    public void testGetTournamentById_Failure_NotFound() throws Exception {
        Long nonExistentTournamentId = 9999L;  // Use a non-existent tournament ID
        URI uri = new URI("http://localhost:" + port + "/tournament/" + nonExistentTournamentId);
    
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    
        System.out.println("Response Body: " + response.getBody());  // Print response body for debugging
    
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());  // Expect a 404 Not Found
        assertTrue(response.getBody().contains("\"error\":\"Not Found\""));
    }

    @Test
    public void testGetAllTournaments_Success() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/tournament");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateTournament_Success() throws Exception {
        // Create a new tournament first so that it can be updated
        Tournament initialTournament = new Tournament();
        initialTournament.setName("Initial Tournament");
        initialTournament.setIsRandom(true);
        initialTournament.setDate(Date.valueOf("2024-01-01"));
        initialTournament.setTime(Time.valueOf("09:00:00"));
        initialTournament.setLocation("Initial Location");
        initialTournament.setOrganizer(1L);
        initialTournament.setDescription("Initial Description");
    
        // Save the initial tournament to the database
        URI createUri = new URI("http://localhost:" + port + "/tournament");
        HttpEntity<Tournament> createEntity = new HttpEntity<>(initialTournament, headers);
        ResponseEntity<Tournament> createResponse = restTemplate.exchange(createUri, HttpMethod.POST, createEntity, Tournament.class);
    
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Long tournamentId = createResponse.getBody().getTournamentId();  // Use the newly created tournament's ID
    
        // Now update the same tournament
        URI updateUri = new URI("http://localhost:" + port + "/tournament/" + tournamentId);
    
        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");
        updatedTournament.setIsRandom(false);
        updatedTournament.setDate(Date.valueOf("2024-01-20"));
        updatedTournament.setTime(Time.valueOf("10:00:00"));
        updatedTournament.setLocation("Updated Location");
        updatedTournament.setOrganizer(2L);
        updatedTournament.setDescription("Updated Description");
    
        HttpEntity<Tournament> updateEntity = new HttpEntity<>(updatedTournament, headers);
        ResponseEntity<Tournament> updateResponse = restTemplate.exchange(updateUri, HttpMethod.PUT, updateEntity, Tournament.class);
    
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated Tournament", updateResponse.getBody().getName());
    }
    
    
    @Test
    public void testUpdateTournament_Failure_NotFound() throws Exception {
        Long nonExistentTournamentId = 9999L;  // Use a non-existent tournament ID
        URI uri = new URI("http://localhost:" + port + "/tournament/" + nonExistentTournamentId);
    
        Tournament tournament = new Tournament();
        tournament.setName("Non-Existent Tournament");
    
        HttpEntity<Tournament> entity = new HttpEntity<>(tournament, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
    
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());  // Expect a 404 Not Found
        assertTrue(response.getBody().contains("\"error\":\"Not Found\""));
    }
    

    @Test
    public void testDeleteTournament_Success() throws Exception {
        // Assuming we have an existing tournament with id 1
        List<Long> deleteList = List.of(1L);

        URI uri = new URI("http://localhost:" + port + "/tournament");
        HttpEntity<List<Long>> entity = new HttpEntity<>(deleteList, headers);

        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteTournament_Failure_NotFound() throws Exception {
        List<Long> deleteList = List.of(9999L);  // Non-existent tournament

        URI uri = new URI("http://localhost:" + port + "/tournament");
        HttpEntity<List<Long>> entity = new HttpEntity<>(deleteList, headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Tournament with id 9999 does not exist"));
    }

    @Test
    public void testSearchTournament_Success() throws Exception {
        URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/tournament/search")
                .queryParam("searchTerm", "Test")
                .build().toUri();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetOngoingTournaments_Success() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/tournament/ongoing");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
