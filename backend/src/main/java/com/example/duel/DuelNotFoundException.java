package com.example.duel;

public class DuelNotFoundException extends RuntimeException {
    public DuelNotFoundException(Long duelId) {
        super("Duel with ID " + duelId + " not found");
    }

    public DuelNotFoundException(String roundName) {
        super("Duel with round name " + roundName + " not found");
    }
}