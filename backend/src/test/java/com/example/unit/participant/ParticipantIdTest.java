package com.example.unit.participant;

import com.example.participant.ParticipantId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ParticipantIdTest {

    @Test
    void testEqualsAndHashCode_SameValues_ReturnsEqual() {
        // arrange
        ParticipantId id1 = new ParticipantId(1L, 2L);
        ParticipantId id2 = new ParticipantId(1L, 2L);
        
        // act and assert
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues_ReturnsNotEqual() {
        // arrange
        ParticipantId id1 = new ParticipantId(1L, 2L);
        ParticipantId id2 = new ParticipantId(1L, 3L);
        ParticipantId id3 = new ParticipantId(2L, 2L);

        // act and assert
        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }

    @Test
    void testConstructor_NullTournamentId_ThrowsException() {
        // act and assert
        assertThrows(NullPointerException.class, () -> new ParticipantId(null, 1L));
    }

    @Test
    void testConstructor_NullProfileId_ThrowsException() {
        // act and assert
        assertThrows(NullPointerException.class, () -> new ParticipantId(1L, null));
    }

    @Test
    void testConstructor_NullValues_ThrowsException() {
        // act and assert
        assertThrows(NullPointerException.class, () -> new ParticipantId(null, null));
    }

    @Test
    void testGetTournament() {
        // arrange
        ParticipantId participantId = new ParticipantId(1L, 2L);
        
        // act and assert
        assertEquals(1L, participantId.getTournament());
    }

    @Test
    void testGetProfile() {
        // arrange
        ParticipantId participantId = new ParticipantId(1L, 2L);
        
        // act and assert
        assertEquals(2L, participantId.getProfile());
    }

    @Test
    void testSetTournament() {
        // arrange
        ParticipantId participantId = new ParticipantId(1L, 2L);
        
        // act
        participantId.setTournament(3L);
        
        // assert
        assertEquals(3L, participantId.getTournament());
    }

    @Test
    void testSetProfile() {
        // arrange
        ParticipantId participantId = new ParticipantId(1L, 2L);
        
        // act
        participantId.setProfile(4L);
        
        // assert
        assertEquals(4L, participantId.getProfile());
    }
}
