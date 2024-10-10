package com.example.participant;

import java.io.Serializable;
import java.util.Objects;

public class ParticipantId implements Serializable {

    private int tournamentId;
    private int userId;

    // Default constructor
    public ParticipantId() {}

    // Parameterized constructor
    public ParticipantId(int tournamentId, int userId) {
        this.tournamentId = tournamentId;
        this.userId = userId;
    }

    // Getters, setters, equals, and hashCode methods
    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return tournamentId == that.tournamentId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournamentId, userId);
    }
}