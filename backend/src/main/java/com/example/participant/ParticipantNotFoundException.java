package com.example.participant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long userId, Long tournamentId) {
        super("Participant with user ID " + userId + " and tournament ID " + tournamentId + " not found");
    }
}
