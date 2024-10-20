package com.example.duel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DuelNotFoundException extends RuntimeException {
    public DuelNotFoundException(String message) {
        super(message);
    }
}
