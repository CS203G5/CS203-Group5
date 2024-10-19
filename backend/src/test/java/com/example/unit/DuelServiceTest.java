package com.example.unit;

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
import com.example.profile.*;
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
    public void testCreateDuel_Success() {
        Profile player1 = new Profile(2L, "test1", "test1@email.com", "test 1", "public", 0.0, "ROLE_PLAYER");
        Profile player2 = new Profile(3L, "test2", "test2@email.com", "test 2", "public", 0.0, "ROLE_PLAYER");
        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
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
                duel.getPlayer1().getProfileId(),
                duel.getPlayer2().getProfileId(),
                duel.getRoundName(),
                duel.getWinner(),
                duel.getTournament().getTournamentId())).thenReturn(duel);

        Duel result = duelService.createDuel(duel);

        assertNotNull(result);
        assertEquals(duel, result);
        verify(duelRepository).createDuel(duel.getPlayer1().getProfileId(), duel.getPlayer2().getProfileId(),
                duel.getRoundName(), duel.getWinner(), duel.getTournament().getTournamentId());
    }

    @Test
    public void testCreateDuel_SamePlayer_Failure() {
        // Setup a single player profile
        Profile player = new Profile(2L, "test1", "test1@email.com", "test 1", "public", 0.0, "ROLE_PLAYER");

        // Create the duel instance with the same player for both player1 and player2
        Duel duel = new Duel();
        duel.setPlayer1(player);
        duel.setPlayer2(player); // Both players are the same
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
                eq(duel.getPlayer1().getProfileId()), 
                eq(duel.getPlayer2().getProfileId()), 
                eq(duel.getRoundName()), 
                eq(duel.getWinner()), 
                eq(duel.getTournament().getTournamentId()) 
        )).thenThrow(new DuelCreationException("Both players cannot be the same."));

        assertThrows(DuelCreationException.class, () -> {
            duelService.createDuel(duel);
        });

        verify(duelRepository).createDuel(
                eq(duel.getPlayer1().getProfileId()), // Ensure it was called with player1 ID
                eq(duel.getPlayer2().getProfileId()), // Ensure it was called with player2 ID (same ID)
                eq(duel.getRoundName()),
                eq(duel.getWinner()),
                eq(duel.getTournament().getTournamentId()));
    }

    @Test
    public void testCreateDuel_AlreadyExists_Failure() {
        Profile player1 = new Profile(2L, "test1", "test1@email.com", "test 1", "public", 0.0, "ROLE_PLAYER");
        Profile player2 = new Profile(3L, "test2", "test2@email.com", "test 2", "public", 0.0, "ROLE_PLAYER");

        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
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
                player1.getProfileId(),
                player2.getProfileId(),
                duel.getRoundName(),
                duel.getWinner(),
                duel.getTournament().getTournamentId()))
                .thenThrow(new DuelCreationException(
                        "A duel with the same players, round, and tournament already exists"));

        assertThrows(DuelCreationException.class, () -> {
            duelService.createDuel(duel);
        });

        verify(duelRepository).createDuel(
                eq(player1.getProfileId()),
                eq(player2.getProfileId()),
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
