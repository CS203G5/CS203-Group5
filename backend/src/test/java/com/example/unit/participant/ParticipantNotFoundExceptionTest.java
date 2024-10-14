package com.example.unit.participant;
import com.example.participant.ParticipantNotFoundException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipantNotFoundExceptionTest {

    @Test
    void testParticipantNotFoundExceptionMessage() {
        Long userId = 1L;
        Long tournamentId = 2L;
        ParticipantNotFoundException exception = new ParticipantNotFoundException(userId, tournamentId);

        assertEquals("Participant with user ID 1 and tournament ID 2 not found", exception.getMessage());
    }
}
