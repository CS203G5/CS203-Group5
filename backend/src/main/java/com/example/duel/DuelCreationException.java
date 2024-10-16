package com.example.duel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuelCreationException extends RuntimeException {
    public DuelCreationException(String message) {
        super(message);
    }

    public DuelCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
