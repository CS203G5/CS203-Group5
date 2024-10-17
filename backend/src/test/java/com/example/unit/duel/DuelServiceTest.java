package com.example.unit.duel;

import com.example.duel.*;
import com.example.tournament.Tournament;
import com.example.tournament.TournamentNotFoundException;
import com.example.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import jakarta.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DuelServiceTest {

    @InjectMocks
    private DuelServiceImpl duelService;

    @Mock
    private DuelRepository duelRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDuel_Success_ReturnsDuel() {
        // Arrange
        Profile player1 = new Profile(1L, "Player 1", "player1@example.com", null, "private", 0.0, "USER");
        Profile player2 = new Profile(2L, "Player 2", "player2@example.com", null, "private", 0.0, "USER");

        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L); // Set a valid tournament ID

        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
        duel.setRoundName("Round 1");
        duel.setTournament(tournament); // Ensure the tournament is set

        // Mocking the behavior for successful duel creation
        when(duelRepository.createDuel(
            eq(player1.getProfileId()),
            eq(player2.getProfileId()),
            eq(duel.getRoundName()),
            any(),
            eq(tournament.getTournamentId())
        )).thenAnswer(invocation -> null); // Simulate success (no return)

        // Act
        Duel createdDuel = duelService.createDuel(duel);

        // Assert
        assertNotNull(createdDuel);
        assertEquals(player1.getProfileId(), createdDuel.getPlayer1().getProfileId());
        assertEquals(player2.getProfileId(), createdDuel.getPlayer2().getProfileId());
        verify(duelRepository).createDuel(
            eq(player1.getProfileId()),
            eq(player2.getProfileId()),
            eq(duel.getRoundName()),
            any(),
            eq(tournament.getTournamentId())
        );
    }

    @Test
    void createDuel_Failure_PlayerMustBeDifferent_ThrowsDuelCreationException() {
        // Arrange
        Profile player1 = new Profile(1L, "Player 1", "player1@example.com", null, "private", 0.0, "USER");
        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player1); // Same player

        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L); // Set a valid tournament ID
        duel.setTournament(tournament);

        // Act & Assert
        DuelCreationException exception = assertThrows(DuelCreationException.class, () -> duelService.createDuel(duel));
        assertEquals("Players must be different", exception.getMessage());

        // Verify that no interaction with the repository occurred
        verifyNoInteractions(duelRepository);
    }


    @Test
    void createDuel_Failure_DuelAlreadyExists_ThrowsDuelCreationException() {
        // Arrange
        Profile player1 = new Profile(1L, "Player 1", "player1@example.com", null, "private", 0.0, "USER");
        Profile player2 = new Profile(2L, "Player 2", "player2@example.com", null, "private", 0.0, "USER");

        Tournament tournament = new Tournament();
        tournament.setTournamentId(1L); // Set a valid tournament ID

        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
        duel.setRoundName("Round 1");
        duel.setTournament(tournament); // Ensure the tournament is set

        // Mocking the behavior when a duel with the same players and round already exists
        doThrow(new DataAccessException("SQL") {
            @Override
            public String getMessage() {
                return "SQL error: A duel with the same players, round, and tournament already exists";
            }
        }).when(duelRepository).createDuel(
            eq(player1.getProfileId()),
            eq(player2.getProfileId()),
            eq(duel.getRoundName()),
            any(),
            eq(tournament.getTournamentId())
        );

        // Act & Assert
        DuelCreationException exception = assertThrows(DuelCreationException.class, () -> duelService.createDuel(duel));
        assertEquals("A duel with the same players, round, and tournament already exists", exception.getMessage());
        verify(duelRepository).createDuel(anyLong(), anyLong(), anyString(), any(), anyLong());
    }

    @Test
    void getDuelById_Success_ReturnsDuel() {
        // Arrange
        Long duelId = 1L;
        Duel duel = new Duel();
        duel.setDuelId(duelId); // Set the ID
        duel.setPlayer1(new Profile(1L, "Player 1", "player1@example.com", null, "private", 0.0, "USER"));
        duel.setPlayer2(new Profile(2L, "Player 2", "player2@example.com", null, "private", 0.0, "USER"));

        // Mocking repository behavior
        when(duelRepository.findById(duelId)).thenReturn(Optional.of(duel));

        // Act
        Duel foundDuel = duelService.getDuelById(duelId);

        // Assert
        assertNotNull(foundDuel);
        assertEquals(duelId, foundDuel.getDuelId());
        verify(duelRepository).findById(duelId);
    }

    @Test
    void getDuelById_NotFound_ThrowsException() {
        // Arrange
        Long duelId = 1L;

        // Mocking repository behavior
        when(duelRepository.findById(duelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> duelService.getDuelById(duelId));
    }

    @Test
    void updateDuel_Success_ReturnsUpdatedDuel() {
        // Arrange
        Long duelId = 1L;
        Duel existingDuel = new Duel();
        existingDuel.setDuelId(duelId); // Set the existing duel ID
        existingDuel.setPlayer1(new Profile(1L, "Player 1", "player1@example.com", null, "private", 0.0, "USER"));
        existingDuel.setPlayer2(new Profile(2L, "Player 2", "player2@example.com", null, "private", 0.0, "USER"));
        
        Duel newDuel = new Duel();
        newDuel.setPlayer1(new Profile(3L, "Player 3", "player3@example.com", null, "private", 0.0, "USER"));
        newDuel.setPlayer2(new Profile(4L, "Player 4", "player4@example.com", null, "private", 0.0, "USER"));

        // Mocking repository behavior
        when(duelRepository.findById(duelId)).thenReturn(Optional.of(existingDuel));
        when(duelRepository.save(any(Duel.class))).thenReturn(newDuel);

        // Act
        Duel updatedDuel = duelService.updateDuel(duelId, newDuel);

        // Assert
        assertNotNull(updatedDuel);
        assertEquals(newDuel.getPlayer1().getProfileId(), updatedDuel.getPlayer1().getProfileId());
        assertEquals(newDuel.getPlayer2().getProfileId(), updatedDuel.getPlayer2().getProfileId());
        verify(duelRepository).findById(duelId);
        verify(duelRepository).save(any(Duel.class));
    }

    @Test
    void updateDuel_NotFound_ThrowsException() {
        // Arrange
        Long duelId = 1L;
        Duel newDuel = new Duel();
        newDuel.setPlayer1(new Profile(3L, "Player 3", "player3@example.com", null, "private", 0.0, "USER"));
        newDuel.setPlayer2(new Profile(4L, "Player 4", "player4@example.com", null, "private", 0.0, "USER"));

        // Mocking repository behavior
        when(duelRepository.findById(duelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> duelService.updateDuel(duelId, newDuel));
    }

    @Test
    void deleteDuel_Success() {
        // Arrange
        Long duelId = 1L;

        // Mocking repository behavior
        when(duelRepository.existsById(duelId)).thenReturn(true);

        // Act
        duelService.deleteDuel(duelId);

        // Assert
        verify(duelRepository).deleteById(duelId);
    }

    @Test
    void deleteDuel_NotFound_ThrowsException() {
        // Arrange
        Long duelId = 1L;

        // Mocking repository behavior
        when(duelRepository.existsById(duelId)).thenReturn(false);

        // Act & Assert
        assertThrows(DuelNotFoundException.class, () -> duelService.deleteDuel(duelId));
    }
}