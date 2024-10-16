package com.example.participant;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long tournamentId) {
        super("No participants found for tournament ID " + tournamentId);
    }

    public ParticipantNotFoundException(Long userId, Long tournamentId) {
        super("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found");
    }
}
