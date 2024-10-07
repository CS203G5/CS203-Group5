package com.example.speed_climber;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import com.example.duel.Duel;
import com.example.duel.DuelRepository;
import com.example.duel.DuelService;
import com.example.duel.DuelServiceImpl;
import com.example.tournament.Tournament;
import com.example.tournament.TournamentRepository;
import com.example.tournament.TournamentService;
import com.example.tournament.TournamentServiceImpl;


@ExtendWith(MockitoExtension.class)
public class SpeedClimberUnitTests {
    @Mock
    TournamentRepository tournamentRepo;

    @Mock
    DuelRepository duelRepository;

    @InjectMocks
    TournamentServiceImpl tournamentService;
    
    @InjectMocks
    DuelServiceImpl duelService;

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

    public void testGetDuelsByTournament() {
        // Given
        Long tournamentId = 1L;
    
        // Create Duel objects and set the properties
        Duel duel1 = new Duel();
        duel1.setPid1(1L);
        duel1.setPid2(2L);
        duel1.setRoundName("Round 1");
        duel1.setWinner(1L);
    
        Duel duel2 = new Duel();
        duel2.setPid1(3L);
        duel2.setPid2(4L);
        duel2.setRoundName("Round 2");
        duel2.setWinner(3L);
    
        List<Duel> mockDuels = Arrays.asList(duel1, duel2);
    
        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(mockDuels);
    
        // When
        List<Duel> result = duelService.getDuelsByTournament(tournamentId);
    
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }
}   
