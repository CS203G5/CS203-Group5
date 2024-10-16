// package com.example.integration.tournament;

// import static org.junit.jupiter.api.Assertions.*;

// import java.net.URI;
// import java.sql.Date;
// import java.sql.Time;
// import java.time.LocalDateTime;
// import java.util.Collections;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;

// import com.example.tournament.Tournament;
// import com.example.tournament.TournamentRepository;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// class TournamentIntegrationTest {

//     @LocalServerPort
//     private int port;

//     private final String baseUrl = "http://localhost:";

//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Autowired
//     private TournamentRepository tournamentRepository;

//     @AfterEach
//     void tearDown() {
//         tournamentRepository.deleteAll();
//     }

//     @Test
//     public void getTournaments_Success() throws Exception {
//         tournamentRepository.save(new Tournament("Test Tournament"));

//         URI uri = new URI(baseUrl + port + "/tournaments");
//         ResponseEntity<Tournament[]> result = restTemplate.getForEntity(uri, Tournament[].class);

//         assertEquals(200, result.getStatusCode().value());
//         assertTrue(result.getBody().length > 0);
//     }

//     @Test
//     public void getTournament_ValidId_Success() throws Exception {
//         Tournament savedTournament = tournamentRepository.save(new Tournament("Test Tournament"));
//         URI uri = new URI(baseUrl + port + "/tournaments/" + savedTournament.getTournament_id());

//         ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

//         assertEquals(200, result.getStatusCode().value());
//         assertEquals(savedTournament.getName(), result.getBody().getName());
//     }

//     @Test
//     public void getTournament_InvalidId_Failure() throws Exception {
//         URI uri = new URI(baseUrl + port + "/tournaments/999"); // Assuming 999 does not exist

//         ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

//         assertEquals(404, result.getStatusCode().value());
//     }

//     @Test
//     public void addTournament_Success() throws Exception {
//         URI uri = new URI(baseUrl + port + "/tournaments");
//         Tournament tournament = new Tournament("New Tournament");
//         tournament.setIsRandom(true);
//         tournament.setDate(Date.valueOf("2024-10-01"));
//         tournament.setTime(Time.valueOf("10:00:00"));
//         tournament.setLocation("Location B");
//         tournament.setDescription("Description");
//         tournament.setModifiedAt(LocalDateTime.now());

//         ResponseEntity<Tournament> result = restTemplate.postForEntity(uri, tournament, Tournament.class);

//         assertEquals(201, result.getStatusCode().value());
//         assertEquals(tournament.getName(), result.getBody().getName());
//     }

//     @Test
//     public void deleteTournament_ValidId_Success() throws Exception {
//         Tournament savedTournament = tournamentRepository.save(new Tournament("Test Tournament"));
//         URI uri = new URI(baseUrl + port + "/tournaments/" + savedTournament.getTournament_id());

//         ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);

//         assertEquals(200, result.getStatusCode().value());
//         assertFalse(tournamentRepository.findById(savedTournament.getTournament_id()).isPresent());
//     }

//     @Test
//     public void deleteTournament_InvalidId_Failure() throws Exception {
//         URI uri = new URI(baseUrl + port + "/tournaments/999"); // Assuming 999 does not exist

//         ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);

//         assertEquals(404, result.getStatusCode().value());
//     }
// }
