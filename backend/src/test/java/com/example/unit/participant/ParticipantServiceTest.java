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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Collections;

class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private Tournament tournament;

    @Mock
    private Profile profile;

    private Participant participant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        participant = new Participant();
        participant.setWin(5);
        participant.setLose(2);
    }

    void mockRepositoryReturnNull(String methodName, Long id) {
        switch (methodName) {
            case "getParticipantsByUserId":
                when(participantRepository.getParticipantsByUserId(id)).thenReturn(null);
                break;
            case "getParticipantsByTournamentId":
                when(participantRepository.getParticipantsByTournamentId(id)).thenReturn(null);
                break;
            default:
                when(participantRepository.findAll()).thenReturn(null);
        }
    }

    void mockRepositoryReturnEmptyList(String methodName, Long id) {
        switch (methodName) {
            case "getParticipantsByUserId":
                when(participantRepository.getParticipantsByUserId(id)).thenReturn(Collections.emptyList());
                break;
            case "getParticipantsByTournamentId":
                when(participantRepository.getParticipantsByTournamentId(id)).thenReturn(Collections.emptyList());
                break;
            default:
                when(participantRepository.findAll()).thenReturn(Collections.emptyList());
        }
    }

    void mockRepositoryThrowException(String methodName, Long id) {
        switch (methodName) {
            case "getParticipantsByUserId":
                when(participantRepository.getParticipantsByUserId(id)).thenThrow(new RuntimeException("Database error"));
                break;
            case "getParticipantsByTournamentId":
                when(participantRepository.getParticipantsByTournamentId(id)).thenThrow(new RuntimeException("Database error"));
                break;
            case "deleteById":
                doThrow(new RuntimeException("Database error")).when(participantRepository).deleteById(new ParticipantId(id, id));
                break;
            default:
                when(participantRepository.findAll()).thenThrow(new RuntimeException("Database error"));
        }
    }

    @Test
    void testGetAllParticipants() {
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.findAll()).thenReturn(participants);

        List<Participant> result = participantService.getAllParticipants();

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin());
        assertEquals(2, result.get(0).getLose());

        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testGetAllParticipants_WhenRepositoryReturnsNull() {
        mockRepositoryReturnNull("findAll", null);

        List<Participant> result = participantService.getAllParticipants();

        assertNull(result);
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testGetAllParticipants_WhenRepositoryReturnsEmptyList() {
        mockRepositoryReturnEmptyList("findAll", null);

        List<Participant> result = participantService.getAllParticipants();

        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testSaveParticipant() {
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = participantService.saveParticipant(participant);

        assertEquals(5, result.getWin());
        assertEquals(2, result.getLose());

        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testSaveParticipant_WhenRepositoryReturnsNull() {
        when(participantRepository.save(participant)).thenReturn(null);

        Participant result = participantService.saveParticipant(participant);

        assertNull(result);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testSaveParticipant_WhenRepositoryThrowsException() {
        when(participantRepository.save(participant)).thenThrow(new RuntimeException("Database save error"));

        assertThrows(RuntimeException.class, () -> participantService.saveParticipant(participant));
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testGetParticipantsByUserId() {
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.getParticipantsByUserId(1L)).thenReturn(participants);

        List<Participant> result = participantService.getParticipantsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin());
        assertEquals(2, result.get(0).getLose());

        verify(participantRepository, times(1)).getParticipantsByUserId(1L);
    }

    @Test
    void testGetParticipantsByUserId_WhenRepositoryReturnsNull() {
        mockRepositoryReturnNull("getParticipantsByUserId", 1L);

        List<Participant> result = participantService.getParticipantsByUserId(1L);

        assertNull(result);
        verify(participantRepository, times(1)).getParticipantsByUserId(1L);
    }

    @Test
    void testGetParticipantsByUserId_WhenRepositoryReturnsEmptyList() {
        mockRepositoryReturnEmptyList("getParticipantsByUserId", 1L);

        List<Participant> result = participantService.getParticipantsByUserId(1L);

        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).getParticipantsByUserId(1L);
    }

    @Test
    void testGetParticipantsByUserId_WhenRepositoryThrowsException() {
        mockRepositoryThrowException("getParticipantsByUserId", 1L);

        assertThrows(RuntimeException.class, () -> participantService.getParticipantsByUserId(1L));
        verify(participantRepository, times(1)).getParticipantsByUserId(1L);
    }

    @Test
    void testGetParticipantsByTournamentId() {
        List<Participant> participants = Arrays.asList(participant);
        when(participantRepository.getParticipantsByTournamentId(1L)).thenReturn(participants);

        List<Participant> result = participantService.getParticipantsByTournamentId(1L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getWin());
        assertEquals(2, result.get(0).getLose());

        verify(participantRepository, times(1)).getParticipantsByTournamentId(1L);
    }

    @Test
    void testGetParticipantsByTournamentId_WhenRepositoryReturnsNull() {
        mockRepositoryReturnNull("getParticipantsByTournamentId", 1L);

        List<Participant> result = participantService.getParticipantsByTournamentId(1L);

        assertNull(result);
        verify(participantRepository, times(1)).getParticipantsByTournamentId(1L);
    }

    @Test
    void testGetParticipantsByTournamentId_WhenRepositoryReturnsEmptyList() {
        mockRepositoryReturnEmptyList("getParticipantsByTournamentId", 1L);

        List<Participant> result = participantService.getParticipantsByTournamentId(1L);

        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).getParticipantsByTournamentId(1L);
    }

    @Test
    void testGetParticipantsByTournamentId_WhenRepositoryThrowsException() {
        mockRepositoryThrowException("getParticipantsByTournamentId", 1L);

        assertThrows(RuntimeException.class, () -> participantService.getParticipantsByTournamentId(1L));
        verify(participantRepository, times(1)).getParticipantsByTournamentId(1L);
    }
}