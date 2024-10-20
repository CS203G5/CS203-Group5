package com.example.unit.tournament;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentController;
import com.example.tournament.TournamentNotFoundException;
import com.example.tournament.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

class TournamentControllerTest {

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private TournamentController tournamentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Tournament testTournament;

    @ControllerAdvice
    public class TestExceptionHandler {

        @ExceptionHandler(TournamentNotFoundException.class)
        public ResponseEntity<String> handleTournamentNotFoundException(TournamentNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    
        // Register the JavaTimeModule to handle LocalDateTime serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule());
    
        // Set up MockMvc with TestExceptionHandler to handle exceptions like TournamentNotFoundException
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController)
                .setControllerAdvice(new TestExceptionHandler())  // Add the custom exception handler here
                .build();
    
        testTournament = new Tournament(1L, "Test Tournament", true, Date.valueOf("2024-01-01"), null, "Test Location", 1L, "Test Description", LocalDateTime.now(), null);
    }

    // Test for Get All Tournaments
    @Test
    void getAllTournament_ReturnsListOfTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.findAll()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).findAll();
    }

    @Test
    void getAllTournament_ReturnsEmptyList() throws Exception {
        when(tournamentService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tournament"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tournamentService, times(1)).findAll();
    }

    // Test for Get Tournament by Id
    @Test
    void getTournamentById_ReturnsTournament() throws Exception {
        when(tournamentService.findById(1L)).thenReturn(testTournament);

        mockMvc.perform(get("/tournament/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tournament"));

        verify(tournamentService, times(1)).findById(1L);
    }

    @Test
    void getTournamentById_NotFound() throws Exception {
        // Mock TournamentNotFoundException to simulate not found case
        when(tournamentService.findById(1L)).thenThrow(new TournamentNotFoundException(1L));
    
        mockMvc.perform(get("/tournament/1"))
                .andExpect(status().isNotFound())  // Expecting 404 status
                .andExpect(content().string("Tournament with ID 1 not found"));
    
        verify(tournamentService, times(1)).findById(1L);
    }

    // Test for Get Tournament by Organizer
    @Test
    void getTournamentByOrganizer_ReturnsTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getTournamentByOrganizer(1L)).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/organizer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].organizer_id").value(1L));

        verify(tournamentService, times(1)).getTournamentByOrganizer(1L);
    }

    @Test
    void getTournamentByOrganizer_ReturnsEmptyList() throws Exception {
        when(tournamentService.getTournamentByOrganizer(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tournament/organizer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tournamentService, times(1)).getTournamentByOrganizer(1L);
    }

    // Test for Ongoing Tournaments
    @Test
    void getOngoingTournaments_ReturnsOngoingTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getOngoingTournaments()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/ongoing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).getOngoingTournaments();
    }

    // Test for Fuzzy Search
    @Test
    void fuzzySearchTournament_ReturnsFilteredTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.fuzzySearchTournament("Test")).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/search")
                .param("searchTerm", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).fuzzySearchTournament("Test");
    }

    @Test
    void fuzzySearchTournament_NoSearchTerm_ReturnsAllTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.findAll()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).findAll();
    }

    @Test
    void fuzzySearchTournament_ReturnsEmptyList() throws Exception {
        when(tournamentService.fuzzySearchTournament("Nonexistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tournament/search")
                .param("searchTerm", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tournamentService, times(1)).fuzzySearchTournament("Nonexistent");
    }

    // Test for Tournament by Date
    @Test
    void getTournamentByDate_ReturnsFilteredTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getTournamentByDate(any(), any())).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/filter")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).getTournamentByDate(any(), any());
    }

    @Test
    void getTournamentByDate_ReturnsEmptyList() throws Exception {
        when(tournamentService.getTournamentByDate(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tournament/filter")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tournamentService, times(1)).getTournamentByDate(any(), any());
    }

    @Test
    void getTournamentByDate_ReturnsAllTournaments_WhenDatesAreNull() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.findAll()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).findAll();
    }


    // Test for Sorted Tournaments
    @Test
    void getTournamentBySorted_ReturnsSortedTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getTournamentBySorted("modified_at", "desc")).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/sorted")
                .param("sortBy", "modified_at")
                .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).getTournamentBySorted("modified_at", "desc");
    }

    @Test
    void getTournamentBySorted_ReturnsDefaultSortedTournaments_WhenParamsAreNull() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getTournamentBySorted("modified_at", "desc")).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).getTournamentBySorted("modified_at", "desc");
    }

    // Test for Matching Algo
    @Test
    void getTournamentByMatchingAlgo_ReturnsFilteredTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.getTournamentByMatchingAlgo(true)).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/matching")
                .param("isRandom", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].is_random").value(true));

        verify(tournamentService, times(1)).getTournamentByMatchingAlgo(true);
    }

    @Test
    void getTournamentByMatchingAlgo_NoParam_ReturnsAllTournaments() throws Exception {
        List<Tournament> tournaments = Arrays.asList(testTournament);
        when(tournamentService.findAll()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament/matching"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));

        verify(tournamentService, times(1)).findAll();
    }

    // Test for Add Tournament
    @Test
    void addTournament_Success() throws Exception {
        when(tournamentService.save(any(Tournament.class))).thenReturn(testTournament);

        mockMvc.perform(post("/tournament")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTournament)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tournament"));

        verify(tournamentService, times(1)).save(any(Tournament.class));
    }

    @Test
    void addTournament_InvalidInput() throws Exception {
        // Create an invalid tournament with missing required fields (name is null)
        Tournament invalidTournament = new Tournament();

        // Perform the POST request and capture the result
        MvcResult result = mockMvc.perform(post("/tournament")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTournament)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Print response for debugging
        System.out.println("Response: " + result.getResponse().getContentAsString());

        // Ensure that the service's save method was never called due to validation failure
        verify(tournamentService, never()).save(any(Tournament.class));
    }

    // Test for Update Tournament
    @Test
    void updateTournament_Success() throws Exception {
        when(tournamentService.update(eq(1L), any(Tournament.class))).thenReturn(testTournament);

        mockMvc.perform(put("/tournament/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTournament)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tournament"));

        verify(tournamentService, times(1)).update(eq(1L), any(Tournament.class));
    }

    @Test
    void updateTournament_NotFound() throws Exception {
        when(tournamentService.update(eq(1L), any(Tournament.class))).thenThrow(new TournamentNotFoundException(1L));
    
        mockMvc.perform(put("/tournament/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTournament)))
                .andExpect(status().isNotFound())  // Expecting 404 status
                .andExpect(content().string("Tournament with ID 1 not found"));
    
        verify(tournamentService, times(1)).update(eq(1L), any(Tournament.class));
    }

    // Test for Delete Tournament
    @Test
    void deleteTournament_Success() throws Exception {
        doNothing().when(tournamentService).deleteById(anyList());

        mockMvc.perform(delete("/tournament")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1L, 2L))))
                .andExpect(status().isOk());

        verify(tournamentService, times(1)).deleteById(anyList());
    }

    @Test
    void deleteTournament_Failure() throws Exception {
        doThrow(new TournamentNotFoundException(1L)).when(tournamentService).deleteById(anyList());
    
        mockMvc.perform(delete("/tournament")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1L))))
                .andExpect(status().isNotFound())  // Expecting 404 status
                .andExpect(content().string("Tournament with ID 1 not found"));
    
        verify(tournamentService, times(1)).deleteById(anyList());
    }
}