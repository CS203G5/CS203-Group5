package com.example.unit.participant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.participant.Participant;
import com.example.participant.ParticipantRepository;
import com.example.participant.ParticipantService;

import java.util.Optional;

public class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private Participant existingParticipant;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        existingParticipant = new Participant();
        existingParticipant.setUserId(1);
        existingParticipant.setTournamentId(101);
        existingParticipant.setWin(5);
        existingParticipant.setLose(3);
        existingParticipant.setScore(100);
    }

    @Test
    public void testUpdateParticipant_Success() {
        // Given
        int userId = 1;
        Participant updatedDetails = new Participant();
        updatedDetails.setTournamentId(102);
        updatedDetails.setWin(6);
        updatedDetails.setLose(4);
        updatedDetails.setScore(150);

        when(participantRepository.findById(userId)).thenReturn(Optional.of(existingParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(existingParticipant);

        // When
        Participant result = participantService.updateParticipant(userId, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(102, result.getTournamentId());
        assertEquals(6, result.getWin());
        assertEquals(4, result.getLose());
        assertEquals(150, result.getScore());
        verify(participantRepository).findById(userId);
        verify(participantRepository).save(existingParticipant);
    }

    @Test
    public void testUpdateParticipant_NotFound() {
        // Given
        int userId = 999; // Non-existing user ID
        Participant updatedDetails = new Participant();

        when(participantRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Participant result = participantService.updateParticipant(userId, updatedDetails);

        // Then
        assertNull(result);
        verify(participantRepository).findById(userId);
        verify(participantRepository, never()).save(any(Participant.class));
    }
}
