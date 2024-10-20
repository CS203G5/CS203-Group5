package com.example.unit.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantRepository;
import com.example.participant.ParticipantService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void testGetAllParticipants() {
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantRepository.findAll()).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantService.getAllParticipants();

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testSaveParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant savedParticipant = participantService.saveParticipant(participant);

        assertNotNull(savedParticipant);
        assertEquals(participant, savedParticipant);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testGetParticipantsByUserId() {
        Long userId = 1L;
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantRepository.getParticipantsByUserId(userId)).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantService.getParticipantsByUserId(userId);

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantRepository, times(1)).getParticipantsByUserId(userId);
    }

    @Test
    void testGetParticipantsByTournamentId() {
        Long tournamentId = 1L;
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantRepository.getParticipantsByTournamentId(tournamentId)).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantService.getParticipantsByTournamentId(tournamentId);

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantRepository, times(1)).getParticipantsByTournamentId(tournamentId);
    }

    @Test
    void testDeleteById() {
        ParticipantId participantId = new ParticipantId(1L, 2L);

        doNothing().when(participantRepository).deleteById(participantId);

        participantService.deleteById(participantId);

        verify(participantRepository, times(1)).deleteById(participantId);
    }

    @Test
    void testDeleteByIdThrowsException() {
        ParticipantId participantId = new ParticipantId(1L, 2L);

        doThrow(new IllegalArgumentException("Participant not found")).when(participantRepository).deleteById(participantId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> participantService.deleteById(participantId));

        assertEquals("Participant not found", exception.getMessage());
        verify(participantRepository, times(1)).deleteById(participantId);
    }
}
