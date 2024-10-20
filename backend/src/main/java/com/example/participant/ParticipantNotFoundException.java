package com.example.participant;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long userId, Long tournamentId) {
        super("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found");
    }
}