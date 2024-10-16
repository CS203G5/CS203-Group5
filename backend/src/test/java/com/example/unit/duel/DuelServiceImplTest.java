package com.example.unit.duel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import com.example.duel.DuelRepository;
import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.duel.DuelServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DuelServiceImplTest {

    @Mock
    private DuelRepository duelRepository;

    @InjectMocks
    private DuelServiceImpl duelServiceImpl;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Success case: Mock repository to return a list of duels
        List<Duel> duelList = Arrays.asList(new Duel(), new Duel());
        when(duelRepository.findAll()).thenReturn(duelList);

        // Call the service method
        List<Duel> result = duelServiceImpl.findAll();

        // Assert that the result contains 2 duels
        assertEquals(2, result.size());
        verify(duelRepository, times(1)).findAll();
    }

    @Test
    void testGetDuelById_Success() {
        // Success case: Mock repository to return a duel with ID 1L
        Duel duel = new Duel();
        duel.setDuel_id(1L);
        when(duelRepository.findById(1L)).thenReturn(Optional.of(duel));

        // Call the service method
        Duel result = duelServiceImpl.getDuelById(1L);

        // Assert the result is not null and the ID matches
        assertNotNull(result);
        assertEquals(1L, result.getDuel_id());
    }

    @Test
    void testGetDuelById_NotFound() {
        // Failure case: Mock repository to return an empty Optional
        when(duelRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method
        Duel result = duelServiceImpl.getDuelById(1L);

        // Assert that the result is null
        assertNull(result);
    }

    @Test
    void testCreateDuel_Success() {
        // Success case: Mock repository to save and return the duel
        Duel duel = new Duel();
        when(duelRepository.save(any(Duel.class))).thenReturn(duel);

        // Call the service method
        Duel result = duelServiceImpl.createDuel(duel);

        // Assert that the result is not null
        assertNotNull(result);
        verify(duelRepository, times(1)).save(duel);
    }

    @Test
    void testUpdateDuel_Success() {
        // Success case: Mock repository to find and update an existing duel
        Duel existingDuel = new Duel();
        existingDuel.setDuel_id(1L);
        when(duelRepository.findById(1L)).thenReturn(Optional.of(existingDuel));

        // Create a new duel object to simulate updated values
        Duel newDuel = new Duel();
        newDuel.setPid1(2L);
        newDuel.setPid2(3L);
        when(duelRepository.save(any(Duel.class))).thenReturn(existingDuel);

        // Call the service method
        Duel updatedDuel = duelServiceImpl.updateDuel(1L, newDuel);

        // Assert that the updated values are correct
        assertNotNull(updatedDuel);
        assertEquals(2L, updatedDuel.getPid1());
        verify(duelRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateDuel_NotFound() {
        // Failure case: Mock repository to return an empty Optional when finding by ID
        when(duelRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and assert that an exception is thrown
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            duelServiceImpl.updateDuel(1L, new Duel());
        });

        // Assert that the exception message is correct
        assertEquals("Duel not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteDuel_Success() {
        // Success case: Call the service method to delete a duel
        duelServiceImpl.deleteDuel(1L);

        // Verify that the repository deleteById method was called
        verify(duelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDuel_NotFound() {
        // Failure case: Even if the duel does not exist, we are verifying that the method is called
        doNothing().when(duelRepository).deleteById(99L);

        // Call the service method
        duelServiceImpl.deleteDuel(99L);

        // Verify that the repository's deleteById method is still called
        verify(duelRepository, times(1)).deleteById(99L);
    }

    @Test
    void testUpdateDuelResult_Success() {
        // Create a mock Duel object
        Duel existingDuel = new Duel();
        existingDuel.setDuel_id(1L);
        existingDuel.setPid1(100L); // Player 1
        existingDuel.setPid2(200L); // Player 2

        // Mock repository to return this duel when findById(1L) is called
        when(duelRepository.findById(1L)).thenReturn(Optional.of(existingDuel));

        // Create a mock DuelResult where player 2 has a better time than player 1
        DuelResult result = new DuelResult(300L, 400L); // Player 2 wins

        // Call the service method to update the duel result
        Duel updatedDuel = duelServiceImpl.updateDuelResult(1L, result);

        // Assert that the winner is player 2 (pid2)
        assertNotNull(updatedDuel);
        assertEquals(200L, updatedDuel.getWinner()); // Expect Player 2 (200L) to win

        // Verify that the repository methods were called
        verify(duelRepository, times(1)).findById(1L);
        verify(duelRepository, times(1)).save(existingDuel);
    }

    @Test
    void testUpdateDuelResult_NotFound() {
        // Failure case: Mock repository to return an empty Optional when finding by ID
        when(duelRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and assert that an exception is thrown
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            duelServiceImpl.updateDuelResult(1L, new DuelResult());
        });

        // Assert that the exception message is correct
        assertEquals("Duel not found with id: 1", exception.getMessage());
    }
}