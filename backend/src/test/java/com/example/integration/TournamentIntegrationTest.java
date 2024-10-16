package com.example.integration;

import com.example.integration.CognitoAuthUtils;
import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Time;
import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TournamentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    // Helper to get a valid JWT token
    private String getJwtToken() {
        return CognitoAuthUtils.getJwtToken("khairyo", "Hello12.");
    }

    // ########## ADD TOURNAMENT TESTS ##########

    // Success: Adding a valid tournament
    @Test
    public void testAddTournamentSuccess() throws Exception {
        String token = getJwtToken();
        String tournamentJson = "{\"name\":\"Test Tournament\",\"isRandom\":true,\"date\":\"2024-10-01\",\"time\":\"12:00:00\",\"location\":\"Test Location\"}";

        mockMvc.perform(post("/tournament")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tournamentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tournament"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    // Failure: Adding a tournament with missing required fields
    @Test
    public void testAddTournamentFailure() throws Exception {
        String token = getJwtToken();
        String tournamentJson = "{\"isRandom\":true}";  // Missing name

        mockMvc.perform(post("/tournament")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tournamentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ########## GET TOURNAMENT TESTS ##########

    // Success: Fetching all tournaments
    @Test
    public void testGetAllTournamentsSuccess() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/tournament")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Failure: Fetching tournaments without valid token
    @Test
    public void testGetAllTournamentsUnauthorized() throws Exception {
        String invalidToken = "invalid_token";

        mockMvc.perform(get("/tournament")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    // Success: Fetch tournament by valid ID
    @Test
    public void testGetTournamentByIdSuccess() throws Exception {
        String token = getJwtToken();
    
        // Insert a tournament into the test database
        Tournament tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setIsRandom(true);
        tournament.setDate(Date.valueOf("2024-10-01"));
        tournament.setTime(Time.valueOf("12:00:00"));
        tournament.setLocation("Test Location");
    
        // Save the tournament to the test repository (which should use an in-memory or test DB)
        Tournament savedTournament = tournamentRepository.save(tournament);  // Assume you have autowired the repository
    
        // Now, perform the GET request using the ID of the inserted tournament
        mockMvc.perform(get("/tournament/{tid}", savedTournament.getTournament_id())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournament_id").value(savedTournament.getTournament_id()))  // Check that the ID matches
                .andExpect(jsonPath("$.name").value("Test Tournament"));  // Optionally check other fields
    }
    

    // Failure: Fetch tournament by invalid ID
    @Test
    public void testGetTournamentByIdNotFound() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/tournament/{tid}", 999L)  // Assuming ID 999 does not exist
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ########## UPDATE TOURNAMENT TESTS ##########

    // Success: Updating a tournament
    @Test
    public void testUpdateTournamentSuccess() throws Exception {
        String token = getJwtToken();
        String tournamentJson = "{\"name\":\"Updated Tournament\",\"isRandom\":false,\"date\":\"2024-10-02\",\"time\":\"14:00:00\",\"location\":\"Updated Location\"}";

        mockMvc.perform(put("/tournament/{tid}", 1L)  // Assuming ID 1 exists
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tournamentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tournament"))
                .andExpect(jsonPath("$.location").value("Updated Location"));
    }

    // Failure: Updating a non-existing tournament
    @Test
    public void testUpdateTournamentNotFound() throws Exception {
        String token = getJwtToken();
        String tournamentJson = "{\"name\":\"Nonexistent Tournament\",\"isRandom\":false}";

        mockMvc.perform(put("/tournament/{tid}", 999L)  // Assuming ID 999 does not exist
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tournamentJson))
                .andExpect(status().isNotFound());
    }

    // ########## DELETE TOURNAMENT TESTS ##########

    // Success: Deleting a tournament
@Test
public void testDeleteTournamentSuccess() throws Exception {
    String token = getJwtToken();
    
    // Insert a tournament to delete
    Tournament tournament = new Tournament();
    tournament.setName("Test Tournament");
    tournament.setIsRandom(true);
    tournament.setDate(Date.valueOf("2024-10-01"));
    tournament.setTime(Time.valueOf("12:00:00"));
    tournament.setLocation("Test Location");
    tournamentRepository.save(tournament);  // Save to test DB

    String deleteJson = "[" + tournament.getTournament_id() + "]";  // Use ID of inserted tournament

    mockMvc.perform(delete("/tournament")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(deleteJson))
            .andExpect(status().isOk());
}


    // Failure: Deleting a non-existing tournament
    @Test
    public void testDeleteTournamentNotFound() throws Exception {
        String token = getJwtToken();
        String deleteJson = "[999]";  // Assuming ID 999 does not exist
    
        mockMvc.perform(delete("/tournament")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deleteJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tournament with id 999 does not exist"));  // Match exception message
    }
    

    // ########## FILTERING, SORTING, AND SEARCH TESTS ##########

    // Success: Fetch tournaments with sorting
    @Test
    public void testGetTournamentBySortedSuccess() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/tournament/sorted")
                .param("sortBy", "date")
                .param("order", "asc")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Failure: Fetch tournaments with invalid sorting parameters
    @Test
    public void testGetTournamentBySortedInvalidParams() throws Exception {
        String token = getJwtToken();
    
        mockMvc.perform(get("/tournament/sorted")
                .param("sortBy", "invalid_column")  // Invalid sorting column
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());  // Expect bad request for invalid params
    }
    

    // Success: Fetch tournaments with filtering by date range
    @Test
    public void testGetTournamentByDateSuccess() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/tournament/filter")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Failure: Fetch tournaments with invalid date range
    @Test
    public void testGetTournamentByDateInvalidParams() throws Exception {
        String token = getJwtToken();

        mockMvc.perform(get("/tournament/filter")
                .param("startDate", "invalid_date")  // Invalid date format
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
