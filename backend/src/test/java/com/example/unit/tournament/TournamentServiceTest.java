package com.example.unit.tournament;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import com.example.tournament.TournamentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        tournament.setTournamentId(1L);
        tournament.setName("Sample Tournament");
        tournament.setDate(Date.valueOf("2024-10-20"));
        tournament.setIsRandom(true);
    }

    // Test findAll method
    @Test
    void findAll_ReturnsListOfTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        tournaments.add(tournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Tournament", result.get(0).getName());
    }

    @Test
    void findAll_NoTournaments_ReturnsEmptyList() {
        when(tournamentRepository.findAll()).thenReturn(new ArrayList<>());

        List<Tournament> result = tournamentService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test findById method
    @Test
    void findById_TournamentExists_ReturnsTournament() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Tournament result = tournamentService.findById(1L);

        assertNotNull(result);
        assertEquals("Sample Tournament", result.getName());
    }

    @Test
    void findById_TournamentNotFound_ReturnsNull() {
        when(tournamentRepository.findById(2L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.findById(2L);

        assertNull(result);
    }

    // Test save method
    @Test
    void save_ValidTournament_ReturnsSavedTournament() {
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament savedTournament = tournamentService.save(tournament);

        assertNotNull(savedTournament);
        assertEquals("Sample Tournament", savedTournament.getName());
    }

    @Test
    void save_InvalidTournament_ReturnsNull() {
        tournament.setName(null); // Set name to null to simulate invalid input
        when(tournamentRepository.save(any(Tournament.class))).thenThrow(new IllegalArgumentException("Invalid tournament"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.save(tournament);
        });

        assertEquals("Invalid tournament", exception.getMessage());
    }

    // Test update method
    @Test
    void update_TournamentExists_ReturnsUpdatedTournament() {
        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);

        Tournament result = tournamentService.update(1L, updatedTournament);

        assertNotNull(result);
        assertEquals("Updated Tournament", result.getName());
    }

    @Test
    void update_TournamentNotFound_ReturnsNull() {
        Tournament updatedTournament = new Tournament();
        updatedTournament.setName("Updated Tournament");

        when(tournamentRepository.findById(2L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.update(2L, updatedTournament);

        assertNull(result);
    }

    // Test deleteById method
    @Test
    void deleteById_ValidIds_DeletesTournaments() {
        List<Long> idsToDelete = List.of(1L);
        when(tournamentRepository.existsById(1L)).thenReturn(true);

        tournamentService.deleteById(idsToDelete);

        verify(tournamentRepository).deleteById(1L);
    }

    @Test
    void deleteById_InvalidIds_ThrowsException() {
        List<Long> idsToDelete = List.of(2L);
        when(tournamentRepository.existsById(2L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.deleteById(idsToDelete);
        });
        assertEquals("Tournament with id 2 does not exist", exception.getMessage());
    }

    // Test getOngoingTournaments method
    @Test
    void getOngoingTournaments_ReturnsListOfOngoingTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        Tournament ongoingTournament = new Tournament();
        ongoingTournament.setTournamentId(2L);
        ongoingTournament.setName("Ongoing Tournament");
        ongoingTournament.setDate(Date.valueOf(LocalDate.now())); // Set the tournament date to today
    
        tournaments.add(tournament); // Add the sample tournament to the list
        tournaments.add(ongoingTournament); // Add the ongoing tournament
        when(tournamentRepository.findAll()).thenReturn(tournaments);
    
        List<Tournament> result = tournamentService.getOngoingTournaments();
    
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ongoing Tournament", result.get(0).getName());
    }
    

    @Test
    void getOngoingTournaments_NoOngoingTournaments_ReturnsEmptyList() {
        List<Tournament> tournaments = new ArrayList<>();
        Tournament pastTournament = new Tournament();
        pastTournament.setTournamentId(3L);
        pastTournament.setName("Past Tournament");
        pastTournament.setDate(Date.valueOf("2024-10-19")); // Assume this is in the past

        tournaments.add(tournament);
        tournaments.add(pastTournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getOngoingTournaments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
