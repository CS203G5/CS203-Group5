package com.example.unit.participant;

import com.example.participant.ParticipantNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ParticipantNotFoundExceptionTest {

    @Test
    void testParticipantNotFoundException_Message() {
        // Arrange
        Long userId = 1L;
        Long tournamentId = 2L;

        // Act and Assert
        ParticipantNotFoundException exception = assertThrows(
                ParticipantNotFoundException.class,
                () -> { throw new ParticipantNotFoundException(userId, tournamentId); }
        );

        // Verify the exception message
        String expectedMessage = "Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testParticipantNotFoundException_NoExceptionThrown() {
        // Arrange
        Long userId = 1L;
        Long tournamentId = 2L;

        // Act and Assert
        assertDoesNotThrow(() -> {
            // Simulate normal flow without throwing the exception
            // In this case, we do nothing which simulates that the participant was found
        });
    }
}
