package com.example.unit.participant;
import com.example.participant.ParticipantId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ParticipantIdTest {

    @Test
    void testEqualsAndHashCode() {
        ParticipantId id1 = new ParticipantId(1L, 1L);
        ParticipantId id2 = new ParticipantId(1L, 1L);
        ParticipantId id3 = new ParticipantId(1L, 2L);

        // Test equality
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);

        // Test hashCode
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}
