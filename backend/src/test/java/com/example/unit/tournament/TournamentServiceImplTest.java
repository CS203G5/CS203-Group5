package com.example.unit.tournament;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import com.example.tournament.TournamentServiceImpl;
import com.example.tournament.TournamentNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentServiceImplTest {

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private TournamentRepository tournamentRepository;

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
    void testFindAll() {
        List<Tournament> tournaments = Arrays.asList(tournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test Tournament", result.get(0).getName());
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Tournament result = tournamentService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_Failure() {
        when(tournamentRepository.findById(9999L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.findById(9999L);

        assertNull(result);
        verify(tournamentRepository, times(1)).findById(9999L);
    }

    @Test
    void testSaveTournament() {
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentService.save(tournament);

        assertNotNull(result);
        assertEquals("Test Tournament", result.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void testUpdateTournament_Success() {
        // Step 1: Mock findById to return an existing tournament
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        
        // Step 2: Create the updated tournament object
        Tournament updatedTournament = new Tournament(
                "Updated Tournament", 
                false, 
                Date.valueOf("2024-02-01"), 
                Time.valueOf("11:00:00"), 
                "Updated Location", 
                2L, 
                "Updated Description"
        );
        
        // Step 3: Mock the save call to return the updated tournament
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        // Step 4: Call the service update method
        Tournament result = tournamentService.update(1L, updatedTournament);

        // Step 5: Verify that the returned tournament is the updated one
        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());

        // Step 6: Verify interactions with the repository
        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }


    @Test
    void testUpdateTournament_Failure() {
        when(tournamentRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.update(9999L, tournament);
        });

        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void testDeleteTournament_Success() {
        when(tournamentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tournamentRepository).deleteById(1L);

        tournamentService.deleteById(Arrays.asList(1L));

        verify(tournamentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTournament_Failure() {
        when(tournamentRepository.existsById(9999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.deleteById(Arrays.asList(9999L));
        });

        verify(tournamentRepository, never()).deleteById(9999L);
    }
}
