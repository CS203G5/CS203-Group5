// package com.example.integration.participant;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.client.LocalServerPort;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpMethod;

// import java.net.URI;
// import java.util.List;

// import com.example.participant.Participant;
// import com.example.participant.ParticipantRepository;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
// class ParticipantIntegrationTest {

//     @LocalServerPort
//     private int port;

//     private final String baseUrl = "http://localhost:";

//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Autowired
//     private ParticipantRepository participantRepository;

//     @AfterEach
//     void tearDown() {
//         // Clear the database after each test
//         participantRepository.deleteAll();
//     }

//     @Test
//     public void testGetParticipant_InvalidId_Failure() throws Exception {
//         // Given an invalid participant ID
//         URI uri = new URI(baseUrl + port + "/participants/999"); // Assuming 999 does not exist

//         // When we attempt to retrieve it
//         ResponseEntity<Participant> result = restTemplate.getForEntity(uri, Participant.class);

//         // Then we should get a 404 Not Found
//         assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
//     }

//     @Test
//     public void testSaveParticipant_Success() throws Exception {
//         // Given a new participant
//         Participant participant = new Participant();
//         participant.setName("John Doe"); // Adjust based on your Participant fields
//         participant.setTournamentId(1); // Example tournament ID
//         participant.setWin(0);
//         participant.setLose(0);
//         participant.setScore(0);

//         URI uri = new URI(baseUrl + port + "/participants");

//         // When we save the participant
//         ResponseEntity<Participant> result = restTemplate.postForEntity(uri, participant, Participant.class);

//         // Then we should get a 201 Created response
//         assertEquals(HttpStatus.CREATED, result.getStatusCode());
//         assertNotNull(result.getBody().getId());
//         assertEquals(participant.getName(), result.getBody().getName());
//     }

//     @Test
//     public void testUpdateParticipant_Success() throws Exception {
//         // Given an existing participant
//         Participant participant = new Participant();
//         participant.setName("John Doe");
//         participant.setTournamentId(1);
//         participant.setWin(0);
//         participant.setLose(0);
//         participant.setScore(0);
//         Participant savedParticipant = participantRepository.save(participant);

//         // When we update the participant
//         savedParticipant.setName("Jane Doe"); // Update the name
//         URI uri = new URI(baseUrl + port + "/participants/" + savedParticipant.getId());
//         HttpEntity<Participant> requestEntity = new HttpEntity<>(savedParticipant);
//         ResponseEntity<Participant> result = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Participant.class);

//         // Then we should get a 200 OK response
//         assertEquals(HttpStatus.OK, result.getStatusCode());
//         assertEquals("Jane Doe", result.getBody().getName());
//     }

//     @Test
//     public void testDeleteParticipant_Success() throws Exception {
//         // Given an existing participant
//         Participant participant = new Participant();
//         participant.setName("John Doe");
//         participant.setTournamentId(1);
//         participant.setWin(0);
//         participant.setLose(0);
//         participant.setScore(0);
//         Participant savedParticipant = participantRepository.save(participant);

//         // When we delete the participant
//         URI uri = new URI(baseUrl + port + "/participants/" + savedParticipant.getId());
//         restTemplate.delete(uri);

//         // Then we should not be able to retrieve it
//         ResponseEntity<Participant> result = restTemplate.getForEntity(uri, Participant.class);
//         assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
//     }

//     @Test
//     public void testGetAllParticipants_Success() throws Exception {
//         // Given participants
//         Participant participant1 = new Participant();
//         participant1.setName("John Doe");
//         participant1.setTournamentId(1);
//         participant1.setWin(0);
//         participant1.setLose(0);
//         participant1.setScore(0);
//         participantRepository.save(participant1);

//         Participant participant2 = new Participant();
//         participant2.setName("Jane Smith");
//         participant2.setTournamentId(1);
//         participant2.setWin(0);
//         participant2.setLose(0);
//         participant2.setScore(0);
//         participantRepository.save(participant2);

//         // When we get all participants
//         URI uri = new URI(baseUrl + port + "/participants");
//         ResponseEntity<List> result = restTemplate.exchange(uri, HttpMethod.GET, null, List.class);

//         // Then we should get a 200 OK response and the list size should be 2
//         assertEquals(HttpStatus.OK, result.getStatusCode());
//         assertEquals(2, result.getBody().size());
//     }
// }

package com.example.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
    
    @Procedure(name = "getParticipantsByUserId")
    List<Participant> getParticipantsByUserId(@Param("user_id") Long user_id);

    @Procedure(name = "getParticipantsByTournamentId")
    List<Participant> getParticipantsByTournamentId(@Param("tournament_id") Long tournament_id);
}