package com.example.unit.participant;

import com.example.participant.*;
import com.example.tournament.Tournament;
import com.example.profile.Profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService; // The class being tested

    @Mock
    private ParticipantRepository participantRepository; // Mock the repository

    @Mock
    private Tournament tournament; // Mocked Tournament

    @Mock
    private Profile profile; // Mocked Profile

    private Participant participant;

    @BeforeEach
    void setUp() {
        // Initialize Mockito mocks
        MockitoAnnotations.openMocks(this);

        // Create a sample Participant object for testing
        participant = new Participant();
        participant.setWin(5);
        participant.setLose(2);
        // You don't have setters for `tournament` and `profile`, so they need to be set through the constructor if provided
    }

    @Test
    void testGetAllParticipants() {
        // Mock the findAll() method of the repository
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.findAll()).thenReturn(participants);

        // Call the method to be tested
        List<Participant> result = participantService.getAllParticipants();

        // Assertions to verify the expected results
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin()); // Verify win count
        assertEquals(2, result.get(0).getLose()); // Verify lose count

        // Verify that the repository was called exactly once
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testSaveParticipant() {
        // Mock the save() method of the repository
        when(participantRepository.save(participant)).thenReturn(participant);

        // Call the method to be tested
        Participant result = participantService.saveParticipant(participant);

        // Assertions to verify the expected results
        assertEquals(5, result.getWin()); // Verify win count
        assertEquals(2, result.getLose()); // Verify lose count

        // Verify that the repository's save method was called exactly once
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testGetParticipantsByUserId() {
        // Mock the getParticipantsByUserId() method of the repository
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.getParticipantsByUserId(1L)).thenReturn(participants);

        // Call the method to be tested
        List<Participant> result = participantService.getParticipantsByUserId(1L);

        // Assertions to verify the expected results
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin()); // Verify win count
        assertEquals(2, result.get(0).getLose()); // Verify lose count

        // Verify that the repository's getParticipantsByUserId method was called exactly once
        verify(participantRepository, times(1)).getParticipantsByUserId(1L);
    }

    @Test
    void testGetParticipantsByTournamentId() {
        // Mock the getParticipantsByTournamentId() method of the repository
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.getParticipantsByTournamentId(1L)).thenReturn(participants);

        // Call the method to be tested
        List<Participant> result = participantService.getParticipantsByTournamentId(1L);

        // Assertions to verify the expected results
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin()); // Verify win count
        assertEquals(2, result.get(0).getLose()); // Verify lose count

        // Verify that the repository's getParticipantsByTournamentId method was called exactly once
        verify(participantRepository, times(1)).getParticipantsByTournamentId(1L);
    }

    @Test
    void testDeleteById() {
        ParticipantId participantId = new ParticipantId(1L, 1L);
        
        // Mock the deleteById() method of the repository
        doNothing().when(participantRepository).deleteById(participantId);

        // Call the method to be tested
        participantService.deleteById(participantId);

        // Verify that the repository's deleteById method was called exactly once
        verify(participantRepository, times(1)).deleteById(participantId);
    }
}