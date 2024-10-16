package com.example.unit.tournament;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import com.example.tournament.TournamentServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

class TournamentServiceImplTest {

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Mock
    private TournamentRepository tournamentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllSuccess() {
        List<Tournament> tournaments = Arrays.asList(new Tournament("Tournament1"), new Tournament("Tournament2"));
        when(tournamentRepository.findAll()).thenReturn(tournaments);
        
        List<Tournament> result = tournamentService.findAll();
        
        assertEquals(2, result.size());
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        Tournament tournament = new Tournament("Tournament1");
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Tournament result = tournamentService.findById(1L);

        assertNotNull(result);
        assertEquals("Tournament1", result.getName());
        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdFailure() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.findById(1L);

        assertNull(result);
        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveTournamentSuccess() {
        Tournament tournament = new Tournament("Tournament1");
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentService.save(tournament);

        assertNotNull(result);
        assertEquals("Tournament1", result.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void testUpdateTournamentSuccess() {
        Tournament oldTournament = new Tournament("OldName");
        Tournament newTournament = new Tournament("NewName");
        
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(oldTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(newTournament);
        
        Tournament result = tournamentService.update(1L, newTournament);

        assertNotNull(result);
        assertEquals("NewName", result.getName());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void testUpdateTournamentFailure() {
        Tournament newTournament = new Tournament("NewName");
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.update(1L, newTournament);

        assertNull(result);
        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteByIdSuccess() {
        when(tournamentRepository.existsById(1L)).thenReturn(true);

        tournamentService.deleteById(Arrays.asList(1L));

        verify(tournamentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIdFailure() {
        when(tournamentRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.deleteById(Arrays.asList(1L));
        });

        assertEquals("Tournament with id 1 does not exist", exception.getMessage());
        verify(tournamentRepository, times(1)).existsById(1L);
    }
}