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

    // @Test
    // void testGetDuelById_Success() {
    //     // Create and set up a mock duel object
    //     Duel duel = new Duel();
    //     duel.setDuel_id(1L);  // Make sure the duel has the correct ID set
    
    //     // Mock the repository to return the duel when findById(1L) is called
    //     when(duelRepository.findById(1L)).thenReturn(Optional.of(duel));
    
    //     // Call the service method
    //     Duel result = duelServiceImpl.getDuelById(1L);
    
    //     // Assertions to verify the returned result
    //     assertNotNull(result);  // Ensure the duel is not null
    //     assertEquals(1L, result.getDuel_id());  // Ensure the duel ID is correct
    
    //     // Verify the repository interaction
    //     verify(duelRepository, times(1)).findById(1L);
    // }
    
    

    @Test
    void testGetDuelById_NotFound() {
        when(duelRepository.existsById(1L)).thenReturn(false);

        DuelNotFoundException exception = assertThrows(DuelNotFoundException.class, () -> {
            duelServiceImpl.getDuelById(1L);
        });

        assertEquals("Duel with ID 1 not found", exception.getMessage());
        verify(duelRepository, times(1)).existsById(1L);
    }

    // @Test
    // void testCreateDuel_Success() {
    //     when(profile1.getProfileId()).thenReturn(1L);
    //     when(profile2.getProfileId()).thenReturn(2L);
    
    //     Duel duel = new Duel();
    //     duel.setPid1(profile1);
    //     duel.setPid2(profile2);
        
    //     Tournament mockTournament = new Tournament();
    //     mockTournament.setTournamentId(1L); // Set a valid ID
    //     duel.setTournament(mockTournament);
        
    //     // Fix: Make sure the mock returns the expected success message
    //     when(duelRepository.createDuel(anyLong(), anyString(), anyLong(), anyLong(), any())).thenReturn("Duel created successfully");
    
    //     String result = duelServiceImpl.createDuel(duel);
    
    //     // Fix: Check that the result matches the correct success message
    //     assertNotNull(result);
    //     assertEquals("Duel created successfully", result);
    
    //     // Fix: Verify that the createDuel method in the repository was called
    //     verify(duelRepository, times(1)).createDuel(anyLong(), anyString(), eq(1L), eq(2L), any());
    // }
    

    // @Test
    // void testUpdateDuel_Success() {
    //     // Mock profile IDs
    //     when(profile1.getProfileId()).thenReturn(1L);
    //     when(profile2.getProfileId()).thenReturn(2L);
    
    //     Duel existingDuel = new Duel();
    //     existingDuel.setDuel_id(1L);
    
    //     // Ensure the repository returns the existing duel when ID is 1L
    //     when(duelRepository.findById(1L)).thenReturn(Optional.of(existingDuel));
    
    //     Duel newDuel = new Duel();
    //     newDuel.setPid1(profile1);  // Use mocked profile1
    //     newDuel.setPid2(profile2);  // Use mocked profile2
    //     newDuel.setRoundName("Semifinal");
    
    //     Duel updatedDuel = duelServiceImpl.updateDuel(1L, newDuel);
    
    //     assertNotNull(updatedDuel);
    //     assertEquals(1L, updatedDuel.getPid1().getProfileId());
    //     assertEquals(2L, updatedDuel.getPid2().getProfileId());
    //     assertEquals("Semifinal", updatedDuel.getRoundName());
    
    //     verify(duelRepository, times(1)).findById(1L);
    //     verify(duelRepository, times(1)).save(any(Duel.class));
    // }    
    

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