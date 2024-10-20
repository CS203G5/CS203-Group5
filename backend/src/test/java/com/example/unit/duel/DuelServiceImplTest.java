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
import com.example.duel.DuelNotFoundException;
import com.example.profile.Profile;  // Assuming this is your Profile entity
import com.example.tournament.Tournament;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class DuelServiceImplTest {

    @Mock
    private DuelRepository duelRepository;

    @InjectMocks
    private DuelServiceImpl duelServiceImpl;

    @Mock
    private Profile profile1;

    @Mock
    private Profile profile2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Duel> duelList = Arrays.asList(new Duel(), new Duel());
        when(duelRepository.findAll()).thenReturn(duelList);

        List<Duel> result = duelServiceImpl.findAll();

        assertEquals(2, result.size());
        verify(duelRepository, times(1)).findAll();
    }

    @Test
    void testGetDuelsByTournament_Success() {
        List<Duel> duelList = Arrays.asList(new Duel(), new Duel());
        when(duelRepository.getDuelsByTournament(1L)).thenReturn(duelList);

        List<Duel> result = duelServiceImpl.getDuelsByTournament(1L);

        assertEquals(2, result.size());
        verify(duelRepository, times(1)).getDuelsByTournament(1L);
    }

    @Test
    void testGetDuelsByTournament_NoDuelsFound() {
        when(duelRepository.getDuelsByTournament(1L)).thenReturn(Arrays.asList());

        List<Duel> result = duelServiceImpl.getDuelsByTournament(1L);

        assertEquals(0, result.size());
        verify(duelRepository, times(1)).getDuelsByTournament(1L);
    }

    @Test
    void testGetDuelsByRoundName_Success() {
        List<Duel> duelList = Arrays.asList(new Duel(), new Duel());
        when(duelRepository.getDuelsByRoundName("Final")).thenReturn(duelList);

        List<Duel> result = duelServiceImpl.getDuelsByRoundName("Final");

        assertEquals(2, result.size());
        verify(duelRepository, times(1)).getDuelsByRoundName("Final");
    }

    @Test
    void testGetDuelsByRoundName_NoDuelsFound() {
        when(duelRepository.getDuelsByRoundName("Final")).thenReturn(Arrays.asList());

        List<Duel> result = duelServiceImpl.getDuelsByRoundName("Final");

        assertEquals(0, result.size());
        verify(duelRepository, times(1)).getDuelsByRoundName("Final");
    }

    @Test
    void testGetDuelsByPlayer_Success() {
        List<Duel> duelList = Arrays.asList(new Duel(), new Duel());
        when(duelRepository.getDuelsByPlayer(1L)).thenReturn(duelList);

        List<Duel> result = duelServiceImpl.getDuelsByPlayer(1L);

        assertEquals(2, result.size());
        verify(duelRepository, times(1)).getDuelsByPlayer(1L);
    }

    @Test
    void testGetDuelsByPlayer_NoDuelsFound() {
        when(duelRepository.getDuelsByPlayer(1L)).thenReturn(Arrays.asList());

        List<Duel> result = duelServiceImpl.getDuelsByPlayer(1L);

        assertEquals(0, result.size());
        verify(duelRepository, times(1)).getDuelsByPlayer(1L);
    }

    @Test
    void testGetDuelById_Success() {
        // Create and set up a mock duel object
        Duel duel = new Duel();
        duel.setDuel_id(1L);  // Ensure the duel has the correct ID set
    
        // Mock the repository to return true for existsById and the duel for findById
        when(duelRepository.existsById(1L)).thenReturn(true);
        when(duelRepository.findById(1L)).thenReturn(Optional.of(duel));
    
        // Call the service method
        Duel result = duelServiceImpl.getDuelById(1L);
    
        // Assert the result
        assertNotNull(result);
        assertEquals(1L, result.getDuel_id());
    
        // Verify the repository interaction
        verify(duelRepository, times(1)).existsById(1L);
        verify(duelRepository, times(1)).findById(1L);
    }    
    

    @Test
    void testGetDuelById_NotFound() {
        when(duelRepository.existsById(1L)).thenReturn(false);

        DuelNotFoundException exception = assertThrows(DuelNotFoundException.class, () -> {
            duelServiceImpl.getDuelById(1L);
        });

        assertEquals("Duel with ID 1 not found", exception.getMessage());
        verify(duelRepository, times(1)).existsById(1L);
    }

    @Test
    void testCreateDuel_Success() {
        // Set up a valid Profile object with valid IDs
        when(profile1.getProfileId()).thenReturn(1L);
        when(profile2.getProfileId()).thenReturn(2L);

        // Set up a valid Duel object
        Duel duel = new Duel();
        duel.setPid1(profile1);  // Profile 1
        duel.setPid2(profile2);  // Profile 2
        duel.setRoundName("Round 1");
        duel.setWinner(null);  // Assuming winner is not yet decided

        // Set up a valid Tournament object
        Tournament tournament = new Tournament();
        tournament.setTournamentId(100L);  // Set a valid tournament ID
        duel.setTournament(tournament);

        // Mock the repository method to return a success message
        when(duelRepository.createDuel(
            eq(100L),  // Tournament ID
            eq("Round 1"),  // Round name
            eq(1L),  // Profile 1 ID
            eq(2L),  // Profile 2 ID
            isNull()  // Winner (assuming null, can change based on actual scenario)
        )).thenReturn("Duel created successfully");

        // Call the service method
        String result = duelServiceImpl.createDuel(duel);

        // Assert the result
        assertNotNull(result, "Expected non-null message, but got null.");
        assertEquals("Duel created successfully", result);

        // Verify the repository interaction
        verify(duelRepository, times(1)).createDuel(eq(100L), eq("Round 1"), eq(1L), eq(2L), isNull());
    }
    
    
    @Test
    void testUpdateDuel_Success() {
        // Mock profile IDs
        when(profile1.getProfileId()).thenReturn(1L);
        when(profile2.getProfileId()).thenReturn(2L);
    
        // Mock existing Duel
        Duel existingDuel = new Duel();
        existingDuel.setDuel_id(1L);
    
        // Ensure the repository returns the existing duel
        when(duelRepository.findById(1L)).thenReturn(Optional.of(existingDuel));
    
        // Create a new Duel object with updated data
        Duel newDuel = new Duel();
        newDuel.setPid1(profile1);  // Use mocked profile1
        newDuel.setPid2(profile2);  // Use mocked profile2
        newDuel.setRoundName("Semifinal");
    
        // Ensure save operation works correctly
        when(duelRepository.save(any(Duel.class))).thenReturn(existingDuel);
    
        // Call the service method
        Duel updatedDuel = duelServiceImpl.updateDuel(1L, newDuel);
    
        // Assert the updated duel
        assertNotNull(updatedDuel);
        assertEquals(1L, updatedDuel.getPid1().getProfileId());
        assertEquals(2L, updatedDuel.getPid2().getProfileId());
        assertEquals("Semifinal", updatedDuel.getRoundName());
    
        // Verify the repository interaction
        verify(duelRepository, times(1)).findById(1L);
        verify(duelRepository, times(1)).save(any(Duel.class));
    }       
    

    @Test
    void testUpdateDuel_NotFound() {
        when(duelRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            duelServiceImpl.updateDuel(1L, new Duel());
        });

        assertEquals("Duel not found with id: 1", exception.getMessage());
        verify(duelRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateDuelResult_Success_Player1Wins() {
        Duel duel = new Duel();
        duel.setPid1(profile1);  // Use mocked profile1
        duel.setPid2(profile2);  // Use mocked profile2

        DuelResult result = new DuelResult(100L, 200L); // Player 1 wins

        when(duelRepository.findById(1L)).thenReturn(Optional.of(duel));
        when(duelRepository.save(any(Duel.class))).thenReturn(duel);

        Duel updatedDuel = duelServiceImpl.updateDuelResult(1L, result);

        assertNotNull(updatedDuel);
        assertEquals(1L, updatedDuel.getWinner());
        verify(duelRepository, times(1)).findById(1L);
        verify(duelRepository, times(1)).save(duel);
    }

    @Test
    void testUpdateDuelResult_Success_Player2Wins() {
        Duel duel = new Duel();
        duel.setPid1(profile1);  // Use mocked profile1
        duel.setPid2(profile2);  // Use mocked profile2

        DuelResult result = new DuelResult(200L, 100L); // Player 2 wins

        when(duelRepository.findById(1L)).thenReturn(Optional.of(duel));
        when(duelRepository.save(any(Duel.class))).thenReturn(duel);

        Duel updatedDuel = duelServiceImpl.updateDuelResult(1L, result);

        assertNotNull(updatedDuel);
        assertEquals(2L, updatedDuel.getWinner());
        verify(duelRepository, times(1)).findById(1L);
        verify(duelRepository, times(1)).save(duel);
    }

    @Test
    void testUpdateDuelResult_NotFound() {
        when(duelRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            duelServiceImpl.updateDuelResult(1L, new DuelResult());
        });

        assertEquals("Duel not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteDuel_Success() {
        when(duelRepository.existsById(1L)).thenReturn(true);

        duelServiceImpl.deleteDuel(1L);

        verify(duelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDuel_NotFound() {
        when(duelRepository.existsById(1L)).thenReturn(false);

        DuelNotFoundException exception = assertThrows(DuelNotFoundException.class, () -> {
            duelServiceImpl.deleteDuel(1L);
        });

        assertEquals("Duel with ID 1 not found", exception.getMessage());
        verify(duelRepository, times(1)).existsById(1L);
    }
}