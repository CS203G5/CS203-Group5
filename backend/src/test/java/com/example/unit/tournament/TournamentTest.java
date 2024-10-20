package com.example.unit.tournament;

import com.example.tournament.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TournamentTest {

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(tournament);
        assertNull(tournament.getName());
        assertFalse(tournament.getIsRandom());
        assertNull(tournament.getDate());
        assertNull(tournament.getTime());
        assertNull(tournament.getLocation());
        assertNull(tournament.getOrganizer());
        assertNull(tournament.getDescription());
        assertNull(tournament.getDuels()); // Should be null initially
    }

    @Test
    void testParameterizedConstructor() {
        Date date = new Date(System.currentTimeMillis());
        Time time = new Time(System.currentTimeMillis());
        tournament = new Tournament("Tournament Name", true, date, time, "Location", 123L, "Description");

        assertEquals("Tournament Name", tournament.getName());
        assertTrue(tournament.getIsRandom());
        assertEquals(date, tournament.getDate());
        assertEquals(time, tournament.getTime());
        assertEquals("Location", tournament.getLocation());
        assertEquals(123L, tournament.getOrganizer());
        assertEquals("Description", tournament.getDescription());
    }

    @Test
    void testSetAndGetTournamentId() {
        tournament.setTournamentId(100L);
        assertEquals(100L, tournament.getTournamentId());
    }

    @Test
    void testSetAndGetName() {
        tournament.setName("Test Tournament");
        assertEquals("Test Tournament", tournament.getName());
    }

    @Test
    void testSetAndGetIsRandom() {
        tournament.setIsRandom(true);
        assertTrue(tournament.getIsRandom());

        tournament.setIsRandom(false);
        assertFalse(tournament.getIsRandom());
    }

    @Test
    void testSetAndGetDate() {
        Date date = new Date(System.currentTimeMillis());
        tournament.setDate(date);
        assertEquals(date, tournament.getDate());
    }

    @Test
    void testSetAndGetTime() {
        Time time = new Time(System.currentTimeMillis());
        tournament.setTime(time);
        assertEquals(time, tournament.getTime());
    }

    @Test
    void testSetAndGetLocation() {
        tournament.setLocation("New Location");
        assertEquals("New Location", tournament.getLocation());
    }

    @Test
    void testSetAndGetOrganizer() {
        tournament.setOrganizer(456L);
        assertEquals(456L, tournament.getOrganizer());
    }

    @Test
    void testSetAndGetDescription() {
        tournament.setDescription("New Description");
        assertEquals("New Description", tournament.getDescription());
    }

    @Test
    void testSetAndGetDuels() {
        assertNull(tournament.getDuels()); // Initially should be null

        tournament.setDuels(new ArrayList<>());
        assertNotNull(tournament.getDuels());
    }

    @Test
    void testSetAndGetTournamentIdLongMethod() {
        Long expectedId = 123L;
        Long actualId = tournament.getTournamentId(expectedId);
        assertEquals(expectedId, actualId);
    }
}
