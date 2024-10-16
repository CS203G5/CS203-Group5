package com.example.unit.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantNotFoundException;
import com.example.participant.ParticipantService;
import com.example.participant.ParticipantRepository;
import com.example.tournament.Tournament;
import com.example.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllParticipants_ReturnsListOfParticipants() {
        // Arrange
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant()); // Add a dummy participant

        when(participantRepository.findAll()).thenReturn(participants);

        // Act
        List<Participant> result = participantService.getAllParticipants();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantRepository).findAll();
    }

    @Test
    void saveParticipant_NewParticipant_ReturnsSavedParticipant() {
        // Arrange
        Participant participant = new Participant();
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        // Act
        Participant savedParticipant = participantService.saveParticipant(participant);

        // Assert
        assertNotNull(savedParticipant);
        verify(participantRepository).save(participant);
    }

    @Test
    void saveParticipant_NullParticipant_ReturnsNull() {
        // Arrange
        Participant participant = null;
    
        // Act
        Participant savedParticipant = participantService.saveParticipant(participant);
    
        // Assert
        assertNull(savedParticipant);
        verify(participantRepository, never()).save(any(Participant.class)); // Ensure save was never called
    }
    

    @Test
    void getParticipantsByUserId_ReturnsParticipants() {
        // Arrange
        Long userId = 1L;
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant());

        when(participantRepository.getParticipantsByUserId(userId)).thenReturn(participants);

        // Act
        List<Participant> result = participantService.getParticipantsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantRepository).getParticipantsByUserId(userId);
    }

    @Test
    void getParticipantsByUserId_NonExistentUser_ReturnsEmptyList() {
        // Arrange
        Long userId = 999L;
        when(participantRepository.getParticipantsByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        List<Participant> result = participantService.getParticipantsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(participantRepository).getParticipantsByUserId(userId);
    }

    @Test
    void getParticipantsByTournamentId_ReturnsParticipants() {
        // Arrange
        Long tournamentId = 1L;
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant());

        when(participantRepository.getParticipantsByTournamentId(tournamentId)).thenReturn(participants);

        // Act
        List<Participant> result = participantService.getParticipantsByTournamentId(tournamentId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantRepository).getParticipantsByTournamentId(tournamentId);
    }

    @Test
    void getParticipantsByTournamentId_NonExistentTournament_ReturnsEmptyList() {
        // Arrange
        Long tournamentId = 999L;
        when(participantRepository.getParticipantsByTournamentId(tournamentId)).thenReturn(new ArrayList<>());

        // Act
        List<Participant> result = participantService.getParticipantsByTournamentId(tournamentId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(participantRepository).getParticipantsByTournamentId(tournamentId);
    }

    @Test
    void deleteById_ValidParticipantId_DeletesParticipant() {
        // Arrange
        ParticipantId participantId = new ParticipantId(1L, 1L);
        doNothing().when(participantRepository).deleteById(participantId);

        // Act
        assertDoesNotThrow(() -> participantService.deleteById(participantId));

        // Assert
        verify(participantRepository).deleteById(participantId);
    }

    @Test
    void deleteById_NullParticipantId_ThrowsIllegalArgumentException() {
        // Arrange
        ParticipantId participantId = null;
    
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            participantService.deleteById(participantId);
        });
        assertEquals("ParticipantId cannot be null", exception.getMessage());
    }
    

        @Test
    void deleteById_NonExistentParticipantId_ThrowsParticipantNotFoundException() {
        // Arrange
        ParticipantId participantId = new ParticipantId(1L, 999L); // Assume this ID does not exist
        doThrow(new ParticipantNotFoundException(999L, 1L)).when(participantRepository).deleteById(participantId);

        // Act & Assert
        ParticipantNotFoundException exception = assertThrows(ParticipantNotFoundException.class, () -> {
            participantService.deleteById(participantId);
        });
        assertEquals("Participant with user ID 999 and tournament ID 1 not found", exception.getMessage());
        verify(participantRepository).deleteById(participantId);
    }
}
