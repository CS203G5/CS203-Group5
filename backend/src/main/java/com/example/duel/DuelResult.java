package com.example.duel;

import jakarta.persistence.Embeddable;


@Embeddable
public class DuelResult {
    // timing in milliseconds
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
}
