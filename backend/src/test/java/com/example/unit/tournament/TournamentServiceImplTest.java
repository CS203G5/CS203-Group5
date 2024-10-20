package com.example.unit.tournament;

import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import com.example.tournament.TournamentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1"),
            new Tournament("Tournament 2", true, Date.valueOf("2023-01-02"), Time.valueOf("12:00:00"), "Location 2", 2L, "Description 2")
        );
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindById_Success() {
        Tournament tournament = new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1");
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        Tournament result = tournamentService.findById(1L);
        assertNotNull(result);
        assertEquals("Tournament 1", result.getName());
    }

    @Test
    void testFindById_NotFound() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.findById(1L);
        assertNull(result);
    }

    @Test
    void testGetOngoingTournaments_PastAndFutureDates() {
        Tournament pastTournament = new Tournament("Past Tournament", false, Date.valueOf(LocalDate.now().minusDays(2)), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1");
        Tournament futureTournament = new Tournament("Future Tournament", true, Date.valueOf(LocalDate.now().plusDays(2)), Time.valueOf("12:00:00"), "Location 2", 2L, "Description 2");
        Tournament todayTournament = new Tournament("Today Tournament", true, Date.valueOf(LocalDate.now()), Time.valueOf("12:00:00"), "Location 3", 3L, "Description 3");

        when(tournamentRepository.findAll()).thenReturn(Arrays.asList(pastTournament, futureTournament, todayTournament));

        List<Tournament> result = tournamentService.getOngoingTournaments();
        assertEquals(1, result.size()); // Only "Today Tournament" should match
        assertEquals("Today Tournament", result.get(0).getName());
    }

    @Test
    void testGetTournamentByOrganizer() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1")
        );
        when(tournamentRepository.getTournamentByOrganizer(1L)).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getTournamentByOrganizer(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrganizer());
    }

    @Test
    void testFuzzySearchTournament() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1")
        );
        when(tournamentRepository.fuzzySearchTournament("Tournament")).thenReturn(tournaments);

        List<Tournament> result = tournamentService.fuzzySearchTournament("Tournament");
        assertEquals(1, result.size());
        assertEquals("Tournament 1", result.get(0).getName());
    }

    @Test
    void testGetTournamentByDate() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1")
        );
        Date startDate = Date.valueOf("2023-01-01");
        Date endDate = Date.valueOf("2023-01-02");
        when(tournamentRepository.getTournamentByDate(startDate, endDate)).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getTournamentByDate(startDate, endDate);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTournamentBySorted() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1")
        );
        when(tournamentRepository.getTournamentBySorted("name", "asc")).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getTournamentBySorted("name", "asc");
        assertEquals(1, result.size());
    }

    @Test
    void testGetTournamentByMatchingAlgo() {
        List<Tournament> tournaments = Arrays.asList(
            new Tournament("Tournament 1", true, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1")
        );
        when(tournamentRepository.getTournamentByMatchingAlgo(true)).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getTournamentByMatchingAlgo(true);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsRandom());
    }

    @Test
    void testSave() {
        Tournament tournament = new Tournament("Tournament 1", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Location 1", 1L, "Description 1");
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentService.save(tournament);
        assertNotNull(result);
        assertEquals("Tournament 1", result.getName());
    }

    @Test
    void testUpdate_Success() {
        Tournament existingTournament = new Tournament("Old Tournament", false, Date.valueOf("2023-01-01"), Time.valueOf("10:00:00"), "Old Location", 1L, "Old Description");
        Tournament newTournamentInfo = new Tournament("New Tournament", true, Date.valueOf("2023-02-01"), Time.valueOf("12:00:00"), "New Location", 2L, "New Description");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(existingTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(existingTournament);

        Tournament result = tournamentService.update(1L, newTournamentInfo);
        assertNotNull(result);
        assertEquals("New Tournament", result.getName());
    }

    @Test
    void testUpdate_NotFound() {
        Tournament newTournamentInfo = new Tournament("New Tournament", true, Date.valueOf("2023-02-01"), Time.valueOf("12:00:00"), "New Location", 2L, "New Description");
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        Tournament result = tournamentService.update(1L, newTournamentInfo);
        assertNull(result);
    }

    @Test
    void testDeleteById_Success_MultipleIds() {
        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.existsById(2L)).thenReturn(true);
        doNothing().when(tournamentRepository).deleteById(any(Long.class));

        tournamentService.deleteById(Arrays.asList(1L, 2L));
        verify(tournamentRepository, times(1)).deleteById(1L);
        verify(tournamentRepository, times(1)).deleteById(2L);
    }

    @Test
    void testDeleteById_TournamentNotFound_MultipleIds() {
        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(tournamentRepository.existsById(2L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.deleteById(Arrays.asList(1L, 2L));
        });

        assertEquals("Tournament with id 2 does not exist", exception.getMessage());
        verify(tournamentRepository, never()).deleteById(2L);
    }
}