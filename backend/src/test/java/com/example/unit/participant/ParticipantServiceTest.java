package com.example.unit.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantRepository;
import com.example.participant.ParticipantService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

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
    void testGetAllParticipantsSuccess() {
        List<Participant> participants = List.of(new Participant());
        when(participantRepository.findAll()).thenReturn(participants);

        List<Participant> result = participantService.getAllParticipants();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetParticipantsByUserIdSuccess() {
        Long userId = 1L;
        List<Participant> participants = List.of(new Participant());
        when(participantRepository.getParticipantsByUserId(userId)).thenReturn(participants);

        List<Participant> result = participantService.getParticipantsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetParticipantsByUserIdFailure() {
        Long userId = 1L;
        when(participantRepository.getParticipantsByUserId(userId)).thenReturn(List.of());

        List<Participant> result = participantService.getParticipantsByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveParticipantSuccess() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = participantService.saveParticipant(participant);

        assertNotNull(result);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testDeleteByIdSuccess() {
        ParticipantId participantId = new ParticipantId(1L, 1L);
        doNothing().when(participantRepository).deleteById(participantId);

        participantService.deleteById(participantId);

        verify(participantRepository, times(1)).deleteById(participantId);
    }
}