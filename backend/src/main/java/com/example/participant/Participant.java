package com.example.participant;

import com.example.tournament.Tournament;
import com.example.profile.Profile;

import jakarta.persistence.*;

@Entity
@Table(name = "participant")
@IdClass(ParticipantId.class)
public class Participant {

    @Id
    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "tournament_id")
    private Tournament tournament;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "profileId")
    private Profile profile;

    private int win;
    private int lose;

    public Tournament getTournament() {
        return tournament;
    }

    public Profile getProfile() {
        return profile;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }
}