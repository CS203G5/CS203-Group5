package com.example.unit.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantController;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantNotFoundException;
import com.example.participant.ParticipantService;

import com.example.duel.DuelService;
import com.example.tournament.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private DuelService duelService;

    @InjectMocks
    private ParticipantController participantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllParticipants() {
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantService.getAllParticipants()).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantController.getAllParticipants();

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantService, times(1)).getAllParticipants();
    }

    @Test
    void testGetParticipantsByTournamentId() {
        Long tournamentId = 1L;
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantService.getParticipantsByTournamentId(tournamentId)).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantController.getParticipantsByTournamentId(tournamentId);

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantService, times(1)).getParticipantsByTournamentId(tournamentId);
    }

    @Test
    void testGetParticipantsByUserId() {
        Long userId = 1L;
        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        when(participantService.getParticipantsByUserId(userId)).thenReturn(Arrays.asList(participant1, participant2));

        List<Participant> participants = participantController.getParticipantsByUserId(userId);

        assertNotNull(participants);
        assertEquals(2, participants.size());
        verify(participantService, times(1)).getParticipantsByUserId(userId);
    }

    @Test
    void testRegisterParticipant() {
        Participant participant = new Participant();
        when(participantService.saveParticipant(participant)).thenReturn(participant);

        Participant savedParticipant = participantController.registerParticipant(participant);

        assertNotNull(savedParticipant);
        verify(participantService, times(1)).saveParticipant(participant);
    }

    @Test
    void testDeleteParticipantSuccess() {
        Long userId = 1L;
        Long tournamentId = 2L;
        ParticipantId participantId = new ParticipantId(tournamentId, userId);

        doNothing().when(participantService).deleteById(participantId);

        assertDoesNotThrow(() -> participantController.deleteParticipant(userId, tournamentId));

        verify(participantService, times(1)).deleteById(participantId);
    }

    @Test
    void testDeleteParticipantNotFound() {
        Long userId = 1L;
        Long tournamentId = 2L;
        ParticipantId participantId = new ParticipantId(tournamentId, userId);

        doThrow(new EmptyResultDataAccessException(1)).when(participantService).deleteById(participantId);

        ParticipantNotFoundException exception = assertThrows(ParticipantNotFoundException.class,
                () -> participantController.deleteParticipant(userId, tournamentId));

        assertEquals("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found", exception.getMessage());
        verify(participantService, times(1)).deleteById(participantId);
    }
}
