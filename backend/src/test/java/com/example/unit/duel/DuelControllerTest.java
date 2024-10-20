package com.example.unit.duel;

import com.example.duel.DuelController;
import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.duel.DuelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DuelControllerTest {

    @InjectMocks
    DuelController duelController;

    @Mock
    DuelService duelService;

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDuelsByTournament_NoTid() {
        List<Duel> duels = new ArrayList<>();
        when(duelService.findAll()).thenReturn(duels);

        ResponseEntity<List<Duel>> response = duelController.getDuelsByTournament(null);
        List<Duel> result = response.getBody();

        assertNotNull(result);
        assertEquals(duels, result);
        verify(duelService, times(1)).findAll();
    }

    // @Test
    // void testGetDuelsByTournament_WithTid() {
    //     Long tid = 1L;
    //     List<Duel> duels = new ArrayList<>();
        
    //     // Mock the duelService to return an empty list or a list of duels
    //     when(duelService.getDuelsByTournament(tid)).thenReturn(duels);
    
    //     // Call the controller method
    //     ResponseEntity<List<Duel>> response = duelController.getDuelsByTournament(tid);
    //     List<Duel> result = response.getBody();
    
    //     // Assert that the result is not null and equals the mocked result
    //     assertNotNull(result);
    //     assertEquals(duels, result);
        
    //     // Verify that the service was called once with the correct ID
    //     verify(duelService, times(1)).getDuelsByTournament(tid);
    // }    

    @Test
    void testGetDuelById() {
        Long did = 1L;
        Duel duel = new Duel();
        when(duelService.getDuelById(did)).thenReturn(duel);

        ResponseEntity<Duel> response = duelController.getDuelById(did);
        Duel result = response.getBody();

        assertNotNull(result);
        assertEquals(duel, result);
        verify(duelService, times(1)).getDuelById(did);
    }

    @Test
    void testGetDuelsByRoundName_NoRoundName() {
        List<Duel> duels = new ArrayList<>();
        when(duelService.findAll()).thenReturn(duels);

        ResponseEntity<List<Duel>> response = duelController.getDuelsByRoundName(null);
        List<Duel> result = response.getBody();

        assertNotNull(result);
        assertEquals(duels, result);
        verify(duelService, times(1)).findAll();
    }

    @Test
    void testGetDuelsByRoundName_WithRoundName() {
        String roundName = "Final";
        List<Duel> duels = new ArrayList<>();
        when(duelService.getDuelsByRoundName(roundName)).thenReturn(duels);

        ResponseEntity<List<Duel>> response = duelController.getDuelsByRoundName(roundName);
        List<Duel> result = response.getBody();

        assertNotNull(result);
        assertEquals(duels, result);
        verify(duelService, times(1)).getDuelsByRoundName(roundName);
    }

    @Test
    void testGetDuelsByPlayer_NoPid() {
        List<Duel> duels = new ArrayList<>();
        when(duelService.findAll()).thenReturn(duels);

        ResponseEntity<List<Duel>> response = duelController.getDuelsByPlayer(null);
        List<Duel> result = response.getBody();

        assertNotNull(result);
        assertEquals(duels, result);
        verify(duelService, times(1)).findAll();
    }

    @Test
    void testGetDuelsByPlayer_WithPid() {
        Long pid = 1L;
        List<Duel> duels = new ArrayList<>();
        when(duelService.getDuelsByPlayer(pid)).thenReturn(duels);

        ResponseEntity<List<Duel>> response = duelController.getDuelsByPlayer(pid);
        List<Duel> result = response.getBody();

        assertNotNull(result);
        assertEquals(duels, result);
        verify(duelService, times(1)).getDuelsByPlayer(pid);
    }

    @Test
    void testCreateDuel_Success() {
        String successMessage = "Duel created successfully";
    
        // Create a new Duel object
        Duel duel = new Duel();
    
        // Mock the service layer behavior
        when(duelService.createDuel(any(Duel.class))).thenReturn(successMessage);
    
        // Call the controller's createDuel method
        ResponseEntity<String> result = duelController.createDuel(duel);
    
        // Verify the result and status
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(successMessage, result.getBody());
    
        // Verify the service method was called once
        verify(duelService, times(1)).createDuel(any(Duel.class));
    }

    @Test
    void testUpdateDuel() {
        Long did = 1L;
        Duel duel = new Duel();
        when(duelService.updateDuel(did, duel)).thenReturn(duel);

        ResponseEntity<Duel> response = duelController.updateDuel(did, duel);
        Duel result = response.getBody();

        assertNotNull(result);
        assertEquals(duel, result);
        verify(duelService, times(1)).updateDuel(did, duel);
    }

    @Test
    void testUpdateDuelResult() {
        Long did = 1L;
        Duel duel = new Duel();
        DuelResult result = new DuelResult(100L, 200L);
        when(duelService.updateDuelResult(did, result)).thenReturn(duel);

        ResponseEntity<Duel> response = duelController.updateDuelResult(did, result);
        Duel updatedDuel = response.getBody();

        assertNotNull(updatedDuel);
        assertEquals(duel, updatedDuel);
        verify(duelService, times(1)).updateDuelResult(did, result);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/duel" + did + "/score", duel);
    }

    @Test
    void testDeleteDuel() {
        Long did = 1L;

        duelController.deleteDuel(did);

        verify(duelService, times(1)).deleteDuel(did);
    }
}