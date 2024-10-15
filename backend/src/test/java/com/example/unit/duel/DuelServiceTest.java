package com.example.unit.duel;

import static org.mockito.ArgumentMatchers.eq;
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
import com.example.tournament.*;

import java.sql.*;

@ExtendWith(MockitoExtension.class)
public class DuelServiceTest {

    @Mock
    DuelRepository duelRepository;

    @InjectMocks
    DuelServiceImpl duelService;

    @Test
    public void testGetDuelsByTournament() {
        Long tournamentId = 1L;
        Long player1 = 2L;
        Long player2 = 3L;

        Duel duel1 = new Duel();
        duel1.setPid1(player1);
        duel1.setPid2(player2);
        duel1.setRoundName("Round 1");
        duel1.setWinner(1L);

        Duel duel2 = new Duel();
        duel2.setPid1(player1);
        duel2.setPid2(player2);
        duel2.setRoundName("Round 2");
        duel2.setWinner(1L);

        when(duelRepository.existsById(tournamentId)).thenReturn(true);
        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(Arrays.asList(duel1, duel2));

        List<Duel> output = duelService.getDuelsByTournament(tournamentId);

        assertNotNull(output);
        assertEquals(2, output.size());
        assertEquals(duel1, output.get(0));
        assertEquals(duel2, output.get(1));
        verify(duelRepository).existsById(tournamentId);
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }

    @Test
    public void testGetDuelsByTournamentEmpty() {
        Long tournamentId = 1L;
        when(duelRepository.existsById(tournamentId)).thenReturn(true);
        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(Arrays.asList());

        List<Duel> output = duelService.getDuelsByTournament(tournamentId);

        assertNotNull(output);
        assertEquals(0, output.size());
        verify(duelRepository).existsById(tournamentId);
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }

    @Test
    public void testGetDuelsByTournamentNotFound() {
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
        Long pid1 = 2L;
        Long pid2 = 3L;
        DuelResult result = new DuelResult(5345L, 13452L);
        Tournament tournament = new Tournament("Test Tournament", true, Date.valueOf("2023-12-25"), Time.valueOf("10:00:00"), "Test Location", 1L, "Test Test");

        Duel duel = new Duel(null, pid1, pid2, "Round 1", result, result.getWinnerId(), tournament);
        duel.setPid1(pid1);
        duel.setPid2(pid2);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);

        when(duelRepository.existsById(duelId)).thenReturn(true);
        when(duelRepository.findById(duelId)).thenReturn(java.util.Optional.of(duel));

        Duel output = duelService.getDuelById(duelId);

        assertNotNull(output);
        assertEquals(duel, output);
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
    public void testCreateDuel_Success() {
        Long player1 = 2L;
        Long player2 = 3L;        Duel duel = new Duel();
        duel.setPid1(player1);
        duel.setPid2(player2);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);
        duel.setTournament(new Tournament(
                "Test Tournament",
                true,
                Date.valueOf("2023-12-25"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Test"));

        when(duelRepository.createDuel(
                duel.getPid1(),
                duel.getPid2(),
                duel.getRoundName(),
                duel.getWinner(),
                duel.getTournament().getTournamentId())).thenReturn(duel);

        Duel output = duelService.createDuel(duel);

        assertNotNull(output);
        assertEquals(duel, output);
        verify(duelRepository).createDuel(duel.getPid1(), duel.getPid2(),
                duel.getRoundName(), duel.getWinner(), duel.getTournament().getTournamentId());
    }

    @Test
    public void testCreateDuel_SamePlayer_Failure() {
        Long player = 2L;

        Duel duel = new Duel();
        duel.setPid1(player);
        duel.setPid2(player); // Both players are the same
        duel.setRoundName("Round 1");
        duel.setWinner(1L);
        duel.setTournament(new Tournament(
                "Test Tournament",
                true,
                Date.valueOf("2023-12-25"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Test"));

        when(duelRepository.createDuel(
                eq(duel.getPid1()), 
                eq(duel.getPid2()), 
                eq(duel.getRoundName()), 
                eq(duel.getWinner()), 
                eq(duel.getTournament().getTournamentId()) 
        )).thenThrow(new DuelCreationException("Both players cannot be the same."));

        assertThrows(DuelCreationException.class, () -> {
            duelService.createDuel(duel);
        });

        verify(duelRepository).createDuel(
                eq(duel.getPid1()), // Ensure it was called with player1 ID
                eq(duel.getPid2()), // Ensure it was called with player2 ID (same ID)
                eq(duel.getRoundName()),
                eq(duel.getWinner()),
                eq(duel.getTournament().getTournamentId()));
    }

    @Test
    public void testCreateDuel_AlreadyExists_Failure() {
        Long player1 = 2L;
        Long player2 = 3L;        

        Duel duel = new Duel();
        duel.setPid1(player1);
        duel.setPid2(player2);
        duel.setRoundName("Round 1");
        duel.setWinner(1L);
        duel.setTournament(new Tournament(
                "Test Tournament",
                true,
                Date.valueOf("2023-12-25"),
                Time.valueOf("10:00:00"),
                "Test Location",
                1L,
                "Test Test"));

        when(duelRepository.createDuel(
                duel.getPid1(),
                duel.getPid2(),
                duel.getRoundName(),
                duel.getWinner(),
                duel.getTournament().getTournamentId()))
                .thenThrow(new DuelCreationException(
                        "A duel with the same players, round, and tournament already exists"));

        assertThrows(DuelCreationException.class, () -> {
            duelService.createDuel(duel);
        });

        verify(duelRepository).createDuel(
                eq(duel.getPid1()),
                eq(duel.getPid2()),
                eq(duel.getRoundName()),
                eq(duel.getWinner()),
                eq(duel.getTournament().getTournamentId()));

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