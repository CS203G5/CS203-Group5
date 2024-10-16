package com.example.unit.tournament;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentController;
import com.example.tournament.TournamentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;

class TournamentControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TournamentController tournamentController;

    @Mock
    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
    }

    @Test
    void testGetAllTournamentsSuccess() throws Exception {
        List<Tournament> tournaments = Arrays.asList(new Tournament("Tournament1"), new Tournament("Tournament2"));
        when(tournamentService.findAll()).thenReturn(tournaments);

        mockMvc.perform(get("/tournament"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Tournament1"));
        
        verify(tournamentService, times(1)).findAll();
    }

    @Test
    void testGetTournamentByIdSuccess() throws Exception {
        Tournament tournament = new Tournament("Tournament1");
        when(tournamentService.findById(1L)).thenReturn(tournament);

        mockMvc.perform(get("/tournament/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tournament1"));

        verify(tournamentService, times(1)).findById(1L);
    }

    @Test
    void testGetTournamentByIdFailure() throws Exception {
        when(tournamentService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/tournament/1"))
                .andExpect(status().isNotFound());

        verify(tournamentService, times(1)).findById(1L);
    }

    @Test
    void testAddTournamentSuccess() throws Exception {
        Tournament tournament = new Tournament("Tournament1");
        when(tournamentService.save(any(Tournament.class))).thenReturn(tournament);

        mockMvc.perform(post("/tournament")
                .contentType("application/json")
                .content("{\"name\":\"Tournament1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tournament1"));

        verify(tournamentService, times(1)).save(any(Tournament.class));
    }

    @Test
    void testUpdateTournamentSuccess() throws Exception {
        Tournament updatedTournament = new Tournament("UpdatedTournament");
        when(tournamentService.update(anyLong(), any(Tournament.class))).thenReturn(updatedTournament);

        mockMvc.perform(put("/tournament/1")
                .contentType("application/json")
                .content("{\"name\":\"UpdatedTournament\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedTournament"));

        verify(tournamentService, times(1)).update(anyLong(), any(Tournament.class));
    }

    @Test
    void testDeleteTournamentSuccess() throws Exception {
        mockMvc.perform(delete("/tournament")
                .contentType("application/json")
                .content("[1, 2]"))
                .andExpect(status().isOk());

        verify(tournamentService, times(1)).deleteById(anyList());
    }
}