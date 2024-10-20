package com.example.unit.duel;

import com.example.duel.Duel;
import com.example.duel.DuelResult;
import com.example.tournament.Tournament;
import com.example.profile.Profile; // Assuming Profile exists in your project
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DuelTest {

    private Duel duel;
    private DuelResult duelResult;
    private Tournament tournament;
    private Profile profile1;
    private Profile profile2;

    @BeforeEach
    void setUp() {
        // Mock the Profile class
        profile1 = Mockito.mock(Profile.class);
        profile2 = Mockito.mock(Profile.class);

        // Mock tournament (or create an actual instance if required)
        tournament = new Tournament();  // Assuming you have a default constructor for Tournament

        // Set DuelResult
        duelResult = new DuelResult(500L, 600L);

        // Initialize Duel with mocked profiles
        duel = new Duel();
        duel.setDuel_id(1L);
        duel.setPid1(profile1);
        duel.setPid2(profile2);
        duel.setRoundName("Final");
        duel.setResult(duelResult);
        duel.setWinner(101L);
        duel.setTournament(tournament);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1L, duel.getDuelId());
        assertEquals(profile1, duel.getPid1());
        assertEquals(profile2, duel.getPid2());
        assertEquals("Final", duel.getRoundName());
        assertEquals(duelResult, duel.getResult());
        assertEquals(101L, duel.getWinner());
        assertEquals(tournament, duel.getTournament());
    }

    @Test
    void testSetters() {
        DuelResult newResult = new DuelResult(700L, 800L);
        Tournament newTournament = new Tournament();  // Assuming you have a default constructor for Tournament
        Profile newProfile1 = Mockito.mock(Profile.class);
        Profile newProfile2 = Mockito.mock(Profile.class);

        duel.setPid1(newProfile1);
        duel.setPid2(newProfile2);
        duel.setRoundName("Semifinal");
        duel.setResult(newResult);
        duel.setWinner(202L);
        duel.setTournament(newTournament);

        assertEquals(newProfile1, duel.getPid1());
        assertEquals(newProfile2, duel.getPid2());
        assertEquals("Semifinal", duel.getRoundName());
        assertEquals(newResult, duel.getResult());
        assertEquals(202L, duel.getWinner());
        assertEquals(newTournament, duel.getTournament());
    }

    @Test
    void testDefaultConstructor() {
        Duel emptyDuel = new Duel();
        assertNull(emptyDuel.getDuelId());
        assertNull(emptyDuel.getPid1());
        assertNull(emptyDuel.getPid2());
        assertNull(emptyDuel.getRoundName());
        assertNull(emptyDuel.getResult());
        assertNull(emptyDuel.getWinner());
        assertNull(emptyDuel.getTournament());
    }
}
