package com.example.unit.participant;

import com.example.participant.Participant;
import com.example.participant.ParticipantId;
import com.example.participant.ParticipantService;
import com.example.participant.ParticipantNotFoundException;
import com.example.participant.ParticipantController;

import com.example.tournament.TournamentService;
import com.example.duel.DuelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
    void testGetAllParticipantsSuccess() {
        List<Participant> participants = List.of(new Participant());
        when(participantService.getAllParticipants()).thenReturn(participants);

        List<Participant> result = participantController.getAllParticipants();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantService, times(1)).getAllParticipants();
    }

    @Test
    void testGetParticipantsByTournamentIdSuccess() {
        Long tournamentId = 1L;
        List<Participant> participants = List.of(new Participant());
        when(participantService.getParticipantsByTournamentId(tournamentId)).thenReturn(participants);

        List<Participant> result = participantController.getParticipantsByTournamentId(tournamentId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testRegisterParticipantSuccess() {
        Participant participant = new Participant();
        when(participantService.saveParticipant(participant)).thenReturn(participant);

        Participant result = participantController.registerParticipant(participant);

        assertNotNull(result);
        verify(participantService, times(1)).saveParticipant(participant);
    }

    @Test
    void testDeleteParticipantSuccess() {
        Long userId = 1L;
        Long tournamentId = 1L;

        doNothing().when(participantService).deleteById(any(ParticipantId.class));

        participantController.deleteParticipant(userId, tournamentId);

        verify(participantService, times(1)).deleteById(any(ParticipantId.class));
    }

    @Test
    void testDeleteParticipantNotFoundFailure() {
        Long userId = 1L;
        Long tournamentId = 1L;

        doThrow(new EmptyResultDataAccessException(1)).when(participantService).deleteById(any(ParticipantId.class));

        ResponseStatusException thrown = assertThrows(
                ParticipantNotFoundException.class,
                () -> participantController.deleteParticipant(userId, tournamentId),
                "Expected deleteParticipant() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found"));
        verify(participantService, times(1)).deleteById(any(ParticipantId.class));
    }
}
