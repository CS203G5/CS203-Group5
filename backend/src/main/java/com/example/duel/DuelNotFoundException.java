package com.example.duel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DuelNotFoundException extends RuntimeException {
    public DuelNotFoundException(Long duelId) {
        super("Duel with ID " + duelId + " not found");
    }

    public DuelNotFoundException(String roundName) {
        super("Duel with round name " + roundName + " not found");
    }
}