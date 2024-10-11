package com.example.unit.tournament;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepo;

    @InjectMocks
    TournamentServiceImpl tournamentService;

    @Test
    public void testGetTournamentByOrganizer() {
        // Given
        Long organizerId = 1L;
        List<Tournament> mockTournaments = Arrays.asList(new Tournament("Tournament 1"), new Tournament("Tournament 2"));
        when(tournamentRepo.getTournamentByOrganizer(organizerId)).thenReturn(mockTournaments);

        // When
        List<Tournament> result = tournamentService.getTournamentByOrganizer(organizerId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tournamentRepo).getTournamentByOrganizer(organizerId);
    }

    @Test
    public void testGetTournamentByDate() {
        // Given
        Date startDate = new Date();
        Date endDate = new Date();
        List<Tournament> mockTournaments = Arrays.asList(new Tournament("Tournament 1"));
        when(tournamentRepo.getTournamentByDate(startDate, endDate)).thenReturn(mockTournaments);

        // When
        List<Tournament> result = tournamentService.getTournamentByDate(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tournamentRepo).getTournamentByDate(startDate, endDate);
    }
}
