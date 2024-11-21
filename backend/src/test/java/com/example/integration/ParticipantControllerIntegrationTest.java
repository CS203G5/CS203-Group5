package com.example.integration;

import com.example.participant.Participant;
import com.example.participant.ParticipantController;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantService;

import com.example.profile.Profile; // Add this import statement
import com.example.tournament.Tournament;
import com.example.tournament.TournamentService;
import com.example.duel.DuelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ParticipantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ParticipantService participantService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private DuelService duelService;

    @InjectMocks
    private ParticipantController participantController;

    private String jwtToken;  // Store the JWT token for all requests
    private HttpHeaders headers;  // Store the headers with JWT token

    @BeforeEach
    public void setUp() {
        // Generate the JWT token using CognitoAuthUtils
        String username = "khairyo";  // Replace with valid username
        String password = "Hello12."; // Replace with valid password
    
        // Assuming CognitoAuthUtils.getJwtToken throws an exception on failure, wrap in try-catch
        try {
            jwtToken = CognitoAuthUtils.getJwtToken(username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token for test setup", e);
        }
    
        // Set up the headers with the generated JWT token
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);  // Add JWT token to Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    
    @Test
    void testGetAllParticipants() throws Exception {
        when(participantService.getAllParticipants()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/participants")
                .headers(headers)  // Use the prepared headers with JWT token
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetParticipantsByTournamentId() throws Exception {
        Long tournamentId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/participants/tournament/{tournament_id}", tournamentId)
                .headers(headers)  // Use the prepared headers with JWT token
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testRegisterParticipant() throws Exception {
        // Mocking a Profile and Tournament
        Profile profile = new Profile();
        profile.setProfileId(1L); // Assuming your Profile has an ID field
        profile.setUsername("testuser");
        profile.setEmail("testuser@example.com");

        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L);
        tournament.setName("Test Tournament");

        // Create a Participant and associate with Profile and Tournament
        Participant participant = new Participant();
        participant.setProfile(profile); // Set the profile
        participant.setTournament(tournament); // Set the tournament
        participant.setWin(0);
        participant.setLose(0);

        // Mocking the save operation in the service
        when(participantService.saveParticipant(participant)).thenReturn(participant);

        String participantJson = "{ \"win\": 0, \"lose\": 0, \"profile\": { \"profileId\": 1 }, \"tournament\": { \"tournamentId\": 1 } }";

        mockMvc.perform(MockMvcRequestBuilders.post("/participants/register")
                .headers(headers)  // Use the prepared headers with JWT token
                .contentType(MediaType.APPLICATION_JSON)
                .content(participantJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void testDeleteParticipant() throws Exception {
        Long tournamentId = 1L;
        Long userId = 1L;
        ParticipantId participantId = new ParticipantId(tournamentId, userId);

        // Mock service for deleting
        doNothing().when(participantService).deleteById(participantId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/participants/user/{user_id}/tournament/{tournament_id}", userId, tournamentId)
                .headers(headers)  // Use the prepared headers with JWT token
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}