package com.example.duel;

import jakarta.persistence.Embeddable;

@Embeddable
public class DuelResult {
    // Timing in milliseconds
    private Long player1Time;
    private Long player2Time;

    public DuelResult() {
    }

    public DuelResult(Long player1Time, Long player2Time) {
        this.player1Time = player1Time;
        this.player2Time = player2Time;
    }

    public Long getPlayer1Time() {
        return player1Time;
    }

    public void setPlayer1Time(Long player1Time) {
        this.player1Time = player1Time;
    }

    public Long getPlayer2Time() {
        return player2Time;
    }

    public void setPlayer2Time(Long player2Time) {
        this.player2Time = player2Time;
    }

    // Determine winner based on times
    public Long getWinnerId() {
        if (player1Time == null || player2Time == null) {
            return null; // Handle null values
        }
        return player1Time < player2Time ? 1L : 2L;
    }

    public Long getLoserId() {
        if (player1Time == null || player2Time == null) {
            return null; // Handle null values
        }
        return player1Time < player2Time ? 2L : 1L;
    }
}