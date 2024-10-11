package com.example.integration.participant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.net.URI;

import com.example.participant.Participant;
import com.example.participant.ParticipantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ParticipantIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParticipantRepository participantRepository;

    @AfterEach
    void tearDown() {
        // Clear the database after each test
        participantRepository.deleteAll();
    }

    @Test
    public void testSaveParticipant_Success() throws Exception {
        // Given a new participant
        Participant participant = new Participant();
        participant.setTournamentId(1);
        participant.setUserId(1);
        participant.setWin(0);
        participant.setLose(0);
        participant.setScore(0.0);

        URI uri = new URI(baseUrl + port + "/participants");

        // When we save the participant
        ResponseEntity<Participant> result = restTemplate.postForEntity(uri, participant, Participant.class);

        // Then we should get a 201 Created response
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody().getUserId());  // Assuming userId is generated or assigned
        assertEquals(participant.getTournamentId(), result.getBody().getTournamentId());
        assertEquals(participant.getUserId(), result.getBody().getUserId());
    }

    @Test
    public void testGetParticipant_Success() throws Exception {
        // Given an existing participant
        Participant participant = new Participant();
        participant.setTournamentId(1);
        participant.setUserId(1);
        participant.setWin(0);
        participant.setLose(0);
        participant.setScore(0.0);
        participantRepository.save(participant);

        URI uri = new URI(baseUrl + port + "/participants/1/1"); // Assuming tournamentId=1, userId=1

        // When we retrieve the participant
        ResponseEntity<Participant> result = restTemplate.getForEntity(uri, Participant.class);

        // Then we should get a 200 OK response
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(participant.getUserId(), result.getBody().getUserId());
        assertEquals(participant.getTournamentId(), result.getBody().getTournamentId());
    }

    @Test
    public void testUpdateParticipant_Success() throws Exception {
        // Given an existing participant
        Participant participant = new Participant();
        participant.setTournamentId(1);
        participant.setUserId(1);
        participant.setWin(0);
        participant.setLose(0);
        participant.setScore(0.0);
        Participant savedParticipant = participantRepository.save(participant);

        // When we update the participant
        savedParticipant.setWin(1);
        URI uri = new URI(baseUrl + port + "/participants/" + savedParticipant.getTournamentId() + "/" + savedParticipant.getUserId());
        HttpEntity<Participant> requestEntity = new HttpEntity<>(savedParticipant);
        ResponseEntity<Participant> result = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Participant.class);

        // Then we should get a 200 OK response
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getWin());
    }

    @Test
    public void testDeleteParticipant_Success() throws Exception {
        // Given an existing participant
        Participant participant = new Participant();
        participant.setTournamentId(1);
        participant.setUserId(1);
        participant.setWin(0);
        participant.setLose(0);
        participant.setScore(0.0);
        Participant savedParticipant = participantRepository.save(participant);

        // When we delete the participant
        URI uri = new URI(baseUrl + port + "/participants/" + savedParticipant.getTournamentId() + "/" + savedParticipant.getUserId());
        restTemplate.delete(uri);

        // Then we should not be able to retrieve it
        ResponseEntity<Participant> result = restTemplate.getForEntity(uri, Participant.class);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}
