package com.example.participant;

import java.io.Serializable;
import java.util.Objects;

public class ParticipantId implements Serializable {
    private Long tournament;
    private Long profile;

    public ParticipantId() {}

    public ParticipantId(Long tournament, Long profile) {
        this.tournament = tournament;
        this.profile = profile;
    }

    public Long getTournament() {
        return tournament;
    }

    public void setTournament(Long tournament) {
        this.tournament = tournament;
    }

    public Long getProfile() {
        return profile;
    }

    public void setProfile(Long profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return Objects.equals(tournament, that.tournament) &&
               Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournament, profile);
    }
}