package com.example.unit.duel;

import com.example.duel.DuelResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuelResultTest {

    private DuelResult duelResult;

    @BeforeEach
    void setUp() {
        duelResult = new DuelResult(500L, 600L);
    }

    @Test
    void testConstructorAndGetters() {
        DuelResult duelResult = new DuelResult(500L, 600L);
        assertEquals(500L, duelResult.getPlayer1Time());
        assertEquals(600L, duelResult.getPlayer2Time());
    }

    @Test
    void testSetters() {
        duelResult.setPlayer1Time(1000L);
        duelResult.setPlayer2Time(1100L);

        assertEquals(1000L, duelResult.getPlayer1Time());
        assertEquals(1100L, duelResult.getPlayer2Time());
    }

    @Test
    void testDefaultConstructor() {
        DuelResult duelResult = new DuelResult();
        assertNull(duelResult.getPlayer1Time());
        assertNull(duelResult.getPlayer2Time());
    }

    @Test
    void testGetWinnerId() {
        DuelResult duelResult = new DuelResult(500L, 600L);
        assertEquals(1L, duelResult.getWinnerId());

        duelResult.setPlayer1Time(600L);
        duelResult.setPlayer2Time(500L);
        assertEquals(2L, duelResult.getWinnerId());

        // Test with null values
        duelResult.setPlayer1Time(null);
        assertNull(duelResult.getWinnerId());

        duelResult.setPlayer1Time(500L);
        duelResult.setPlayer2Time(null);
        assertNull(duelResult.getWinnerId());
    }

    @Test
    void testGetLoserId() {
        DuelResult duelResult = new DuelResult(500L, 600L);
        assertEquals(2L, duelResult.getLoserId());

        duelResult.setPlayer1Time(600L);
        duelResult.setPlayer2Time(500L);
        assertEquals(1L, duelResult.getLoserId());

        // Test with null values
        duelResult.setPlayer1Time(null);
        assertNull(duelResult.getLoserId());

        duelResult.setPlayer1Time(500L);
        duelResult.setPlayer2Time(null);
        assertNull(duelResult.getLoserId());
    }
}