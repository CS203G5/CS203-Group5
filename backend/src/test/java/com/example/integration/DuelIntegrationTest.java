package com.example.integration;

import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.duel.DuelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DuelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DuelRepository duelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        // Set up JWT token using CognitoAuthUtils
        jwtToken = CognitoAuthUtils.getJwtToken("testUser", "testPassword");

        // Pre-populate database with a Duel for testing
        Duel duel = new Duel();
        duel.setPid1(100L);
        duel.setPid2(200L);
        duel.setRoundName("Quarter Final");
        duelRepository.save(duel);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    public void testCreateDuel_Success() throws Exception {
        Duel duel = new Duel();
        duel.setPid1(101L);
        duel.setPid2(202L);
        duel.setRoundName("Semi Final");

        mockMvc.perform(post("/api/duel")
                .headers(getHeaders())
                .content(objectMapper.writeValueAsString(duel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid1").value(101L))
                .andExpect(jsonPath("$.pid2").value(202L))
                .andExpect(jsonPath("$.roundName").value("Semi Final"));
    }

    @Test
    public void testGetDuelById_Success() throws Exception {
        Duel existingDuel = duelRepository.findAll().get(0);

        mockMvc.perform(get("/api/duel/" + existingDuel.getDuel_id())
                .headers(getHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duel_id").value(existingDuel.getDuel_id()))
                .andExpect(jsonPath("$.pid1").value(existingDuel.getPid1()))
                .andExpect(jsonPath("$.pid2").value(existingDuel.getPid2()))
                .andExpect(jsonPath("$.roundName").value(existingDuel.getRoundName()));
    }

    @Test
    public void testGetDuelById_NotFound() throws Exception {
        mockMvc.perform(get("/api/duel/9999")
                .headers(getHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("")); // no content if not found
    }

    @Test
    public void testUpdateDuelResult_Success() throws Exception {
        Duel existingDuel = duelRepository.findAll().get(0);

        DuelResult duelResult = new DuelResult(500L, 600L); // Player 2 wins

        mockMvc.perform(put("/api/duel/" + existingDuel.getDuel_id() + "/result")
                .headers(getHeaders())
                .content(objectMapper.writeValueAsString(duelResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winner").value(existingDuel.getPid2())); // Player 2 should win
    }

    @Test
    public void testUpdateDuelResult_NotFound() throws Exception {
        DuelResult duelResult = new DuelResult(300L, 400L); // Sample data

        mockMvc.perform(put("/api/duel/9999/result")
                .headers(getHeaders())
                .content(objectMapper.writeValueAsString(duelResult)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteDuel_Success() throws Exception {
        Duel existingDuel = duelRepository.findAll().get(0);

        mockMvc.perform(delete("/api/duel/" + existingDuel.getDuel_id())
                .headers(getHeaders()))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteDuel_NotFound() throws Exception {
        mockMvc.perform(delete("/api/duel/9999")
                .headers(getHeaders()))
                .andExpect(status().isNotFound());
    }
}
