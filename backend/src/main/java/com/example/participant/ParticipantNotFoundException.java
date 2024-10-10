package com.example.participant;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(int userId) {
        super("Participant with ID " + userId + " not found");
    }
}