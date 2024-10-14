package com.example.tournament;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(Long tournamentId) {
        super("Tournament with ID " + tournamentId + " not found for duel with ID " + tournamentId);
    }
}