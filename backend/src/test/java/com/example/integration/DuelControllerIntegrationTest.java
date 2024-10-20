package com.example.integration;

import com.example.integration.CognitoAuthUtils;
import com.example.profile.Profile;
import com.example.tournament.Tournament;
import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.profile.ProfileRepository;
import com.example.tournament.TournamentRepository;
import com.example.duel.DuelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DuelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private DuelRepository duelRepository;

    private Profile player1;
    private Profile player2;
    private Tournament tournament;
    private Duel duel;
    private HttpHeaders headers;
    private String token;

    @BeforeEach
    void setUp() {
        // Create unique players and tournament data
        player1 = new Profile(null, "test1", "test1@email.com", "Player One", "Public", 0.0, "ROLE_PLAYER");
        player2 = new Profile(null, "test2", "test2@email.com", "Player Two", "Public", 0.0, "ROLE_PLAYER");

        // Save the profiles and immediately refetch them to attach to the persistence context
        player1 = profileRepository.save(player1);
        player2 = profileRepository.save(player2);

        // Refetch saved profiles to avoid detached entity issues
        player1 = profileRepository.findById(player1.getProfileId()).orElseThrow();
        player2 = profileRepository.findById(player2.getProfileId()).orElseThrow();

        // Save the tournament
        tournament = tournamentRepository.save(new Tournament(
                "Test Tournament 1",
                true,
                Date.valueOf("2023-12-26"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Organizer"
        ));

        // Create and save the Duel, ensuring that the players are managed entities
        DuelResult result = new DuelResult(3000L, 4000L);
        duel = new Duel();
        duel.setPid1(player1);  // Use the refetched, managed player
        duel.setPid2(player2);  // Use the refetched, managed player
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
        URI uri = UriComponentsBuilder.fromHttpUrl("/api/duel")
                .queryParam("tid", tournament.getTournamentId())
                .build()
                .toUri();

        mockMvc.perform(get(uri)
                .headers(headers))  // Include headers with JWT token
                .andExpect(status().isOk());
    }

    @Test
    public void getDuelById_Success() throws Exception {
        mockMvc.perform(get("/api/duel/" + duel.getDuelId())
                .headers(headers)  // Include JWT token in the request
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createDuel_Success() throws Exception {
        String duelJson = "{ \"round_name\": \"Round 2\", \"pid1\": { \"profileId\": " + player1.getProfileId() + " }, \"pid2\": { \"profileId\": " + player2.getProfileId() + " }, \"tournament\": { \"tournamentId\": " + tournament.getTournamentId() + " }, \"winner\": 1 }";

        mockMvc.perform(post("/api/duel")
                .headers(headers)  // Include JWT token in the request
                .content(duelJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateDuel_Success() throws Exception {
        String updatedDuelJson = "{ \"round_name\": \"Updated Round\", \"pid1\": { \"profileId\": " + player1.getProfileId() + " }, \"pid2\": { \"profileId\": " + player2.getProfileId() + " }, \"winner\": 2 }";

        mockMvc.perform(put("/api/duel/" + duel.getDuelId())
                .headers(headers)  // Include JWT token in the request
                .content(updatedDuelJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteDuel_Success() throws Exception {
        mockMvc.perform(delete("/api/duel/" + duel.getDuelId())
                .headers(headers)  // Include JWT token in the request
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}