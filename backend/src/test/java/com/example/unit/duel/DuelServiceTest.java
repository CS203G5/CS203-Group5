package com.example.unit.duel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.duel.Duel;
import com.example.duel.DuelRepository;
import com.example.duel.DuelResult;
import com.example.duel.DuelServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DuelServiceTest {

    @Mock
    private DuelRepository duelRepository;

    @InjectMocks
    private DuelServiceImpl duelService;

    @Test
    void createDuel_NewDuel_ReturnSavedDuel() {
        // Arrange
        Duel duel = new Duel();
        duel.setDuel_id(1L);
        duel.setPid1(1L);
        duel.setPid2(2L);
        duel.setRoundName("Final");

        // Mock the repository methods
        when(duelRepository.getDuelsByRoundName(duel.getRoundName())).thenReturn(new ArrayList<>());
        when(duelRepository.save(any(Duel.class))).thenReturn(duel);

        // Act
        Duel savedDuel = duelService.createDuel(duel);

        // Assert
        assertNotNull(savedDuel);
        assertEquals(duel.getDuel_id(), savedDuel.getDuel_id());
        verify(duelRepository).getDuelsByRoundName(duel.getRoundName());
        verify(duelRepository).save(duel);
    }

    @Test
    void createDuel_SameRoundName_ReturnNull() {
        // Arrange
        Duel duel = new Duel();
        duel.setDuel_id(1L);
        duel.setRoundName("Duplicate Round");

        List<Duel> existingDuels = new ArrayList<>();
        existingDuels.add(new Duel()); // Simulate existing duel with the same round name
        when(duelRepository.getDuelsByRoundName(duel.getRoundName())).thenReturn(existingDuels);

        // Act
        Duel savedDuel = duelService.createDuel(duel);

        // Assert
        assertNull(savedDuel);
        verify(duelRepository).getDuelsByRoundName(duel.getRoundName());
        verify(duelRepository, never()).save(any(Duel.class)); // Ensure save is not called
    }

    @Test
    void updateDuel_Found_ReturnUpdatedDuel() {
        // Arrange
        Duel existingDuel = new Duel();
        existingDuel.setDuel_id(1L);
        existingDuel.setPid1(1L);
        existingDuel.setPid2(2L);
        existingDuel.setRoundName("Existing Round");

        Duel updatedDuel = new Duel();
        updatedDuel.setPid1(3L);
        updatedDuel.setPid2(4L);
        updatedDuel.setRoundName("Updated Round");

        Long duelId = 1L;

        when(duelRepository.findById(duelId)).thenReturn(Optional.of(existingDuel));
        when(duelRepository.save(any(Duel.class))).thenReturn(existingDuel);

        // Act
        Duel result = duelService.updateDuel(duelId, updatedDuel);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDuel.getRoundName(), result.getRoundName());
        assertEquals(updatedDuel.getPid1(), result.getPid1());
        assertEquals(updatedDuel.getPid2(), result.getPid2());
        verify(duelRepository).findById(duelId);
        verify(duelRepository).save(existingDuel);
    }

    @Test
    void updateDuel_NotFound_ReturnNull() {
        // Arrange
        Duel updatedDuel = new Duel();
        updatedDuel.setRoundName("Updated Duel Title");
        Long duelId = 10L;

        when(duelRepository.findById(duelId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            duelService.updateDuel(duelId, updatedDuel);
        });

        // Assert
        assertEquals("Duel not found with id: " + duelId, exception.getMessage());
        verify(duelRepository).findById(duelId);
        verify(duelRepository, never()).save(any(Duel.class)); // Ensure save is not called
    }

    @Test
    void updateDuelResult_Found_ReturnUpdatedDuel() {
        // Arrange
        Duel existingDuel = new Duel();
        existingDuel.setDuel_id(1L);
        existingDuel.setPid1(1L);
        existingDuel.setPid2(2L);

        DuelResult result = new DuelResult();
        result.setplayer1Time(10L);
        result.setplayer2Time(20L); // Player 2 wins

        Long duelId = 1L;

        when(duelRepository.findById(duelId)).thenReturn(Optional.of(existingDuel));
        when(duelRepository.save(any(Duel.class))).thenReturn(existingDuel);

        // Act
        Duel updatedDuel = duelService.updateDuelResult(duelId, result);

        // Assert
        assertNotNull(updatedDuel);
        assertEquals(existingDuel.getPid2(), updatedDuel.getWinner()); // Player 2 should be the winner
        verify(duelRepository).findById(duelId);
        verify(duelRepository).save(existingDuel);
    }

    @Test
    void updateDuelResult_NotFound_ThrowException() {
        // Arrange
        DuelResult result = new DuelResult();
        Long duelId = 10L;

        when(duelRepository.findById(duelId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            duelService.updateDuelResult(duelId, result);
        });

        // Assert
        assertEquals("Duel not found with id: " + duelId, exception.getMessage());
        verify(duelRepository).findById(duelId);
        verify(duelRepository, never()).save(any(Duel.class)); // Ensure save is not called
    }

    @Test
    void deleteDuel_Found_DeletesDuel() {
        // Arrange
        Long duelId = 1L;

        // Act
        duelService.deleteDuel(duelId);

        // Assert
        verify(duelRepository).deleteDuel(duelId);
    }

    @Test
    void deleteDuel_NotFound_DoesNotThrow() {
        // Arrange
        Long duelId = 10L;
        doThrow(new RuntimeException("Duel not found")).when(duelRepository).deleteDuel(duelId);

        // Act
        duelService.deleteDuel(duelId);

        // Assert
        verify(duelRepository).deleteDuel(duelId);
        // Ensure that exception handling in delete method is working
    }

    @Test
    void findAll_ReturnsAllDuels() {
        // Arrange
        List<Duel> duels = new ArrayList<>();
        duels.add(new Duel());
        duels.add(new Duel());

        when(duelRepository.findAll()).thenReturn(duels);

        // Act
        List<Duel> result = duelService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(duelRepository).findAll();
    }

    @Test
    void getDuelsByTournament_ReturnsDuels() {
        // Arrange
        Long tournamentId = 1L;
        List<Duel> duels = new ArrayList<>();
        duels.add(new Duel());
        duels.add(new Duel());

        when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(duels);

        // Act
        List<Duel> result = duelService.getDuelsByTournament(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(duelRepository).getDuelsByTournament(tournamentId);
    }

    @Test
    void getDuelsByPlayer_ReturnsDuels() {
        // Arrange
        Long playerId = 1L;
        List<Duel> duels = new ArrayList<>();
        duels.add(new Duel());
        duels.add(new Duel());

        when(duelRepository.getDuelsByPlayer(playerId)).thenReturn(duels);

        // Act
        List<Duel> result = duelService.getDuelsByPlayer(playerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(duelRepository).getDuelsByPlayer(playerId);
    }

    @Test
    void getDuelById_Found_ReturnDuel() {
        // Arrange
        Long duelId = 1L;
        Duel duel = new Duel();
        duel.setDuel_id(duelId);

        when(duelRepository.findById(duelId)).thenReturn(Optional.of(duel));

        // Act
        Duel result = duelService.getDuelById(duelId);

        // Assert
        assertNotNull(result);
        assertEquals(duelId, result.getDuel_id());
        verify(duelRepository).findById(duelId);
    }

@Test
void getDuelById_NotFound_ThrowException() {
    // Arrange
    Long duelId = 10L;
    when(duelRepository.findById(duelId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class, () -> duelService.getDuelById(duelId));
}

}
