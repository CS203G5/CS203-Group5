package com.example.unit.participant;

import com.example.participant.Participant;

import com.example.tournament.Tournament;
import com.example.profile.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    private Participant participant;
    private Tournament tournament;
    private Profile profile;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
        profile = new Profile();
        participant = new Participant();
    }

    @Test
    void testSetAndGetTournament() {
        participant.setTournament(tournament);
        assertEquals(tournament, participant.getTournament());
    }

    @Test
    void testSetAndGetProfile() {
        participant.setProfile(profile);
        assertEquals(profile, participant.getProfile());
    }

    @Test
    void testSetAndGetWin() {
        participant.setWin(5);
        assertEquals(5, participant.getWin());
    }

    @Test
    void testSetAndGetLose() {
        participant.setLose(3);
        assertEquals(3, participant.getLose());
    }

    @Test
    void testConstructor() {
        Participant participant = new Participant();
        assertNotNull(participant);
    }
}
