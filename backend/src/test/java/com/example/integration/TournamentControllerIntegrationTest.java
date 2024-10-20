package com.example.integration;

import com.example.integration.CognitoAuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class TournamentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;
    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        // Generate the JWT token using CognitoAuthUtils
        String username = "khairyo";  // Replace with valid username
        String password = "Hello12."; // Replace with valid password

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

    // Test getAllTournament
    @Test
    public void testGetAllTournament() throws Exception {
        mockMvc.perform(get("/tournament")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getTournamentById
    @Test
    public void testGetTournamentById() throws Exception {
        mockMvc.perform(get("/tournament/1")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getTournamentByOrganizer
    @Test
    public void testGetTournamentByOrganizer() throws Exception {
        mockMvc.perform(get("/tournament/organizer/1")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getOngoingTournaments
    @Test
    public void testGetOngoingTournaments() throws Exception {
        mockMvc.perform(get("/tournament/ongoing")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test fuzzySearchTournament
    @Test
    public void testFuzzySearchTournament() throws Exception {
        mockMvc.perform(get("/tournament/search")
                .param("searchTerm", "Test Tournament")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getTournamentByDate
    @Test
    public void testGetTournamentByDate() throws Exception {
        mockMvc.perform(get("/tournament/filter")
                .param("startDate", "2023-12-01")
                .param("endDate", "2023-12-31")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getTournamentBySorted
    @Test
    public void testGetTournamentBySorted() throws Exception {
        mockMvc.perform(get("/tournament/sorted")
                .param("sortBy", "name")
                .param("order", "asc")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test getTournamentByMatchingAlgo
    @Test
    public void testGetTournamentByMatchingAlgo() throws Exception {
        mockMvc.perform(get("/tournament/matching")
                .param("isRandom", "true")
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test addTournament
    @Test
    public void testAddTournament() throws Exception {
        String tournamentJson = "{ \"name\": \"Test Tournament\", \"is_random\": true, \"date\": \"2023-12-26\", \"time\": \"10:00:00\", \"location\": \"Test Location\", \"organizer_id\": 1, \"description\": \"Test Tournament Description\" }";

        mockMvc.perform(post("/tournament")
                .headers(headers)
                .content(tournamentJson))
                .andExpect(status().isOk())  // Assuming success returns 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    // Test updateTournament
    @Test
    public void testUpdateTournament() throws Exception {
        String updatedTournamentJson = "{ \"name\": \"Updated Tournament\", \"is_random\": false, \"date\": \"2023-12-26\", \"time\": \"10:00:00\", \"location\": \"Updated Location\", \"organizer_id\": 1, \"description\": \"Updated Tournament Description\" }";

        mockMvc.perform(put("/tournament/1")
                .headers(headers)
                .content(updatedTournamentJson))
                .andExpect(status().isOk())  // Assuming success returns 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}