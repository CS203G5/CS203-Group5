package com.example.unit.participant;

import com.example.participant.ParticipantId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantIdTest {

    private ParticipantId participantId1;
    private ParticipantId participantId2;

    @BeforeEach
    void setUp() {
        participantId1 = new ParticipantId(1L, 2L);
        participantId2 = new ParticipantId(1L, 2L);
    }

    @Test
    void testConstructor() {
        ParticipantId participantId = new ParticipantId(1L, 2L);
        assertNotNull(participantId);
        assertEquals(1L, participantId.getTournament());
        assertEquals(2L, participantId.getProfile());
    }

    @Test
    void testGetAndSetTournament() {
        participantId1.setTournament(3L);
        assertEquals(3L, participantId1.getTournament());
    }

    @Test
    void testGetAndSetProfile() {
        participantId1.setProfile(4L);
        assertEquals(4L, participantId1.getProfile());
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(participantId1, participantId1); // Same object
    }

    @Test
    void testEqualsDifferentObjectSameValues() {
        assertEquals(participantId1, participantId2); // Different objects but same values
    }

    @Test
    void testNotEqualsNull() {
        assertNotEquals(participantId1, null); // Compared with null
    }

    @Test
    void testNotEqualsDifferentClass() {
        assertNotEquals(participantId1, new Object()); // Compared with different class
    }

    @Test
    void testNotEqualsDifferentValues() {
        ParticipantId differentParticipantId = new ParticipantId(2L, 3L);
        assertNotEquals(participantId1, differentParticipantId); // Different values
    }

    @Test
    void testEqualsNullFields() {
        // One object has null values, the other does not
        ParticipantId participantIdWithNulls = new ParticipantId(null, null);
        ParticipantId participantIdWithNonNulls = new ParticipantId(1L, 2L);
        assertNotEquals(participantIdWithNulls, participantIdWithNonNulls);

        // Both objects have null values
        ParticipantId anotherParticipantIdWithNulls = new ParticipantId(null, null);
        assertEquals(participantIdWithNulls, anotherParticipantIdWithNulls); // Both nulls should be equal
    }

    @Test
    void testHashCodeSameValues() {
        assertEquals(participantId1.hashCode(), participantId2.hashCode()); // Same values, same hashcode
    }

    @Test
    void testHashCodeDifferentValues() {
        ParticipantId differentParticipantId = new ParticipantId(2L, 3L);
        assertNotEquals(participantId1.hashCode(), differentParticipantId.hashCode()); // Different values, different hashcode
    }

    @Test
    void testHashCodeNullFields() {
        ParticipantId participantIdWithNulls = new ParticipantId(null, null);
        ParticipantId anotherParticipantIdWithNulls = new ParticipantId(null, null);
        assertEquals(participantIdWithNulls.hashCode(), anotherParticipantIdWithNulls.hashCode());

        ParticipantId participantIdWithMixedNull = new ParticipantId(1L, null);
        assertNotEquals(participantIdWithNulls.hashCode(), participantIdWithMixedNull.hashCode());
    }

    @Test
    void testDefaultConstructor() {
        ParticipantId participantId = new ParticipantId();
        assertNotNull(participantId);
        participantId.setTournament(1L);
        participantId.setProfile(2L);
        assertEquals(1L, participantId.getTournament());
        assertEquals(2L, participantId.getProfile());
    }
}