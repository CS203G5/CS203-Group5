package com.example.integration;

import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.duel.DuelRepository;
import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.sql.Date;
import java.sql.Time;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DuelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DuelRepository duelRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    private String token;

    @BeforeEach
    public void setUp() {
        token = CognitoAuthUtils.getJwtToken("khairyo", "Hello12.");
    }

    @Test
    public void testCreateDuelSuccess() throws Exception {
        Tournament tournament = createTestTournament();
        String duelJson = "{ \"pid1\": 1, \"pid2\": 2, \"roundName\": \"Round 1\", \"tournament\": { \"tournament_id\": " + tournament.getTournament_id() + " } }";

        mockMvc.perform(post("/api/duel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(duelJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roundName").value("Round 1"))
                .andExpect(jsonPath("$.pid1").value(1))
                .andExpect(jsonPath("$.pid2").value(2));
    }

    @Test
    public void testCreateDuelFailure() throws Exception {
        Tournament tournament = createTestTournament();
        String duelJson = "{ \"roundName\": \"Round 1\", \"tournament\": { \"tournament_id\": " + tournament.getTournament_id() + " } }";
    
        mockMvc.perform(post("/api/duel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(duelJson))
                .andExpect(status().isBadRequest());  // Expect 400 due to missing fields
    }
    

    @Test
    public void testGetDuelByIdSuccess() throws Exception {
        Duel duel = createTestDuel();

        mockMvc.perform(get("/api/duel/{did}", duel.getDuel_id())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duel_id").value(duel.getDuel_id()));
    }

    @Test
    public void testGetDuelByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/duel/{did}", 999L)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDuelSuccess() throws Exception {
        Duel duel = createTestDuel();
        String updatedDuelJson = "{ \"pid1\": 3, \"pid2\": 4, \"roundName\": \"Round 2\" }";

        mockMvc.perform(put("/api/duel/{did}", duel.getDuel_id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedDuelJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid1").value(3))
                .andExpect(jsonPath("$.pid2").value(4))
                .andExpect(jsonPath("$.roundName").value("Round 2"));
    }

    @Test
    public void testUpdateDuelNotFound() throws Exception {
        String updatedDuelJson = "{ \"pid1\": 3, \"pid2\": 4, \"roundName\": \"Round 2\" }";
    
        mockMvc.perform(put("/api/duel/{did}", 999L)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedDuelJson))
                .andExpect(status().isNotFound());  // Expect 404 Not Found
    }
    

    @Test
    public void testUpdateDuelResultSuccess() throws Exception {
        Duel duel = createTestDuel();
        String duelResultJson = "{ \"player1Time\": 1200, \"player2Time\": 1300 }";

        mockMvc.perform(put("/api/duel/{did}/result", duel.getDuel_id())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(duelResultJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.player1Time").value(1200))
                .andExpect(jsonPath("$.result.player2Time").value(1300));
    }
    
    @Test
    public void testDeleteDuelSuccess() throws Exception {
        Duel duel = createTestDuel();

        mockMvc.perform(delete("/api/duel/{did}", duel.getDuel_id())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteDuelNotFound() throws Exception {
        mockMvc.perform(delete("/api/duel/{did}", 999L)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private Duel createTestDuel() {
        Tournament tournament = createTestTournament();
        Duel duel = new Duel();
        duel.setPid1(1L);
        duel.setPid2(2L);
        duel.setRoundName("Round 1");
        duel.setTournament(tournament);
        return duelRepository.save(duel);
    }

    private Tournament createTestTournament() {
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setIsRandom(false);
        tournament.setDate(Date.valueOf("2024-01-01"));
        tournament.setTime(Time.valueOf("10:00:00"));
        tournament.setLocation("Test Location");
        return tournamentRepository.save(tournament);
    }
}
