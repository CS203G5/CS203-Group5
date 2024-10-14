package com.example.unit.duel;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.duel.*;
import com.example.profile.*;
import com.example.tournament.TournamentNotFoundException;

@ExtendWith(MockitoExtension.class)
public class DuelServiceTest {

    @Mock
    DuelRepository duelRepository;

    @InjectMocks
    DuelServiceImpl duelService;

    @Test
    public void testGetDuelsByTournament() {
        Long tournamentId = 1L;
        Profile player = new Profile();

        Duel duel1 = new Duel();
        duel1.setPlayer1(player);
        duel1.setPlayer2(player);
        duel1.setRoundName("Round 1");
        duel1.setWinner(1L);

        Duel duel2 = new Duel();
        duel2.setPlayer1(player);
        duel2.setPlayer2(player);
        duel2.setRoundName("Round 2");
        duel2.setWinner(1L);

        when(duelRepository.existsById(tournamentId)).thenReturn(true);
        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(Arrays.asList(duel1, duel2));

        List<Duel> result = duelService.getDuelsByTournament(tournamentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(duel1, result.get(0));
        assertEquals(duel2, result.get(1));
        verify(duelRepository).existsById(tournamentId);
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }

    @Test
    public void testGetDuelsByTournamentEmpty() {
        Long tournamentId = 1L;
        when(duelRepository.existsById(tournamentId)).thenReturn(true);
        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(Arrays.asList());

        List<Duel> result = duelService.getDuelsByTournament(tournamentId);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(duelRepository).existsById(tournamentId);
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }

    @Test
    public void testGetDuelsByTournamentNotFound(){
        Long tournamentId = 1L;
        when(duelRepository.existsById(tournamentId)).thenReturn(false);

        assertThrows(TournamentNotFoundException.class, () -> {
            duelService.getDuelsByTournament(tournamentId);
        });

        verify(duelRepository).existsById(tournamentId);
    }

    @Test
    public void testGetDuelById() {
        Long duelId = 1L;
        Profile player = new Profile();

        Duel duel = new Duel();
        duel.setPlayer1(player);
        duel.setPlayer2(player);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);

        when(duelRepository.existsById(duelId)).thenReturn(true);
        when(duelRepository.findById(duelId)).thenReturn(java.util.Optional.of(duel));

        Duel result = duelService.getDuelById(duelId);

        assertNotNull(result);
        assertEquals(duel, result);
        verify(duelRepository).existsById(duelId);
        verify(duelRepository).findById(duelId);
    }

    @Test
    public void testGetDuelByIdNotFound() {
        Long duelId = 1L;
        when(duelRepository.existsById(duelId)).thenReturn(false);

        assertThrows(DuelNotFoundException.class, () -> {
            duelService.getDuelById(duelId);
        });

        verify(duelRepository).existsById(duelId);
    }

    @Test
    public void testSaveDuel() {
        Profile player1 = new Profile();
        Profile player2 = new Profile();

        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);

        when(duelRepository.save(duel)).thenReturn(duel);

        Duel result = duelService.createDuel(duel);

        assertNotNull(result);
        assertEquals(duel, result);
        verify(duelRepository).save(duel);
    }

    @Test
    public void testDeleteDuel() {
        Long duelId = 1L;
        when(duelRepository.existsById(duelId)).thenReturn(true);

        duelService.deleteDuel(duelId);

        verify(duelRepository).existsById(duelId);
        verify(duelRepository).deleteById(duelId);
    }

    @Test
    public void testDeleteDuelNotFound() {
        Long duelId = 1L;
        when(duelRepository.existsById(duelId)).thenReturn(false);

        assertThrows(DuelNotFoundException.class, () -> {
            duelService.deleteDuel(duelId);
        });

        verify(duelRepository).existsById(duelId);
    }
}
