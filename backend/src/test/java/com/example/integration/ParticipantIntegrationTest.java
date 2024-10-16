package com.example.integration;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantRepository;
import com.example.participant.ParticipantService;
import com.example.tournament.Tournament;
import com.example.profile.ProfileRepository;
import com.example.profile.Profile;
import com.example.integration.CognitoAuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParticipantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ParticipantService participantService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Clean the repository before each test
        participantRepository.deleteAll();

        // Use CognitoAuthUtils to get a valid JWT token
        jwtToken = CognitoAuthUtils.getJwtToken("khairyo", "Hello12.");
    }

    @Test
    void testGetAllParticipantsSuccess() throws Exception {
        // Arrange
        Participant participant1 = createMockParticipant(1L, 1L, 3, 2);
        Participant participant2 = createMockParticipant(2L, 1L, 5, 1);
        participantRepository.saveAll(List.of(participant1, participant2));

        // Act
        ResultActions resultActions = mockMvc.perform(get("/participants")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].win").value(3))
                .andExpect(jsonPath("$[1].win").value(5));
    }

    @Test
    void testGetParticipantsByTournamentIdSuccess() throws Exception {
        // Arrange
        Long tournamentId = 1L;
        Participant participant = createMockParticipant(1L, tournamentId, 4, 2);
        participantRepository.save(participant);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/participants/tournament/" + tournamentId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].win").value(4));
    }

    @Test
    void testGetParticipantsByUserIdSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        Participant participant = createMockParticipant(userId, 1L, 4, 1);
        participantRepository.save(participant);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/participants/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].win").value(4));
    }

    @Test
    void testRegisterParticipantSuccess() throws Exception {
        // Arrange
        String participantJson = """
        {
            "tournament": { "tournament_id": 1 },
            "profile": { "profileId": 1 },
            "win": 3,
            "lose": 2
        }""";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/participants/register")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(participantJson));

        // Assert
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.win").value(3))
                .andExpect(jsonPath("$.lose").value(2));
    }

    @Test
    void testRegisterParticipantBadRequest() throws Exception {
        // Arrange
        // Missing required fields
        String participantJson = """
        {
            "tournament": { "tournament_id": 1 }
        }""";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/participants/register")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(participantJson));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteParticipantSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        Long tournamentId = 1L;
    
        // Ensure that the Profile and Tournament exist in the database
        createProfile(userId);
        createTournament(tournamentId);
    
        // Create a mock Participant with existing Profile and Tournament
        Participant participant = createMockParticipant(userId, tournamentId, 4, 1);
        participantRepository.save(participant);
    
        // Act
        ResultActions resultActions = mockMvc.perform(delete("/participants/user/" + userId + "/tournament/" + tournamentId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));
    
        // Assert
        resultActions.andExpect(status().isOk());
        assertFalse(participantRepository.existsById(new ParticipantId(tournamentId, userId)));
    }
    

    @Test
    void testDeleteParticipantNotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        Long tournamentId = 999L;
    
        // Act
        ResultActions resultActions = mockMvc.perform(delete("/participants/user/" + userId + "/tournament/" + tournamentId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON));
    
        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found")));
    }
    
    // Helper method to create a mock participant
    private Participant createMockParticipant(Long userId, Long tournamentId, int win, int lose) {
        Tournament tournament = new Tournament();
        tournament.setTournamentId(tournamentId);

        Profile profile = new Profile();
        profile.setProfileId(userId);

        Participant participant = new Participant();
        participant.setTournament(tournament);
        participant.setProfile(profile);
        participant.setWin(win);
        participant.setLose(lose);
        
        return participant;
    }

    // Helper method to create a profile
    private void createProfile(Long profileId) {
        Profile profile = new Profile();
        profile.setProfileId(profileId);
        profile.setName("Test Profile");
        profileRepository.save(profile);
    }

    // Helper method to create a tournament
    private void createTournament(Long tournamentId) {
        Tournament tournament = new Tournament();
        tournament.setTournamentId(tournamentId);
        tournament.setName("Test Tournament");
        tournamentRepository.save(tournament);
}

}
