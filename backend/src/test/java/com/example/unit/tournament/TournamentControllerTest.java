package com.example.unit.tournament;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.tournament.TournamentController;
import com.example.tournament.Tournament;
import com.example.tournament.TournamentNotFoundException;
import com.example.tournament.TournamentService;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentControllerTest {

    @InjectMocks
    private TournamentController tournamentController;

    @Mock
    private TournamentService tournamentService;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tournament = new Tournament(
                "Test Tournament", 
                true, 
                Date.valueOf("2024-01-01"), 
                Time.valueOf("10:00:00"), 
                "Test Location", 
                1L, 
                "Test Description"
        );
    }

    @Test
    void testGetAllTournaments() {
        List<Tournament> tournaments = Arrays.asList(tournament);
        when(tournamentService.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentController.getAllTournament();

        assertEquals(1, result.size());
        assertEquals("Test Tournament", result.get(0).getName());
        verify(tournamentService, times(1)).findAll();
    }

    @Test
    void testGetTournamentById_Success() {
        when(tournamentService.findById(1L)).thenReturn(tournament);

        Tournament result = tournamentController.getTournamentById(1L);

        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        verify(tournamentService, times(1)).findById(1L);
    }

    @Test
    void testGetTournamentById_Failure() {
        when(tournamentService.findById(9999L)).thenReturn(null);

        assertThrows(TournamentNotFoundException.class, () -> {
            tournamentController.getTournamentById(9999L);
        });

        verify(tournamentService, times(1)).findById(9999L);
    }

    @Test
    void testAddTournament() {
        when(tournamentService.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentController.addTournament(tournament);

        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        verify(tournamentService, times(1)).save(tournament);
    }

    @Test
    void testUpdateTournament_Success() {
        Tournament updatedTournament = new Tournament(
                "Updated Tournament", 
                false, 
                Date.valueOf("2024-02-01"), 
                Time.valueOf("11:00:00"), 
                "Updated Location", 
                2L, 
                "Updated Description"
        );
        when(tournamentService.update(1L, updatedTournament)).thenReturn(updatedTournament);

        Tournament result = tournamentController.updateTournament(1L, updatedTournament);

        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());
        verify(tournamentService, times(1)).update(1L, updatedTournament);
    }

    @Test
    void testUpdateTournament_Failure() {
        Tournament updatedTournament = new Tournament(
                "Updated Tournament", 
                false, 
                Date.valueOf("2024-02-01"), 
                Time.valueOf("11:00:00"), 
                "Updated Location", 
                2L, 
                "Updated Description"
        );
        when(tournamentService.update(9999L, updatedTournament)).thenThrow(new TournamentNotFoundException(9999L));

        assertThrows(TournamentNotFoundException.class, () -> {
            tournamentController.updateTournament(9999L, updatedTournament);
        });

        verify(tournamentService, times(1)).update(9999L, updatedTournament);
    }

    @Test
    void testDeleteTournament_Success() {
        doNothing().when(tournamentService).deleteById(Arrays.asList(1L));

        tournamentController.deleteTournament(Arrays.asList(1L));

        verify(tournamentService, times(1)).deleteById(Arrays.asList(1L));
    }

    @Test
    void testDeleteTournament_Failure() {
        doThrow(new IllegalArgumentException("Tournament with id 9999 does not exist")).when(tournamentService).deleteById(Arrays.asList(9999L));

        assertThrows(IllegalArgumentException.class, () -> {
            tournamentController.deleteTournament(Arrays.asList(9999L));
        });

        verify(tournamentService, times(1)).deleteById(Arrays.asList(9999L));
    }
}
