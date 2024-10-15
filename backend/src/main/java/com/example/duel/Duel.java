package com.example.duel;

import com.example.tournament.Tournament;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "duel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Duel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long duel_id;
    private Long pid1;
    private Long pid2;
    private String roundName;
    @Embedded
    private DuelResult result;
    private Long winner;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    public Long getDuelId() {
        return duel_id;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setPid1(Long pid1) {
        this.pid1 = pid1;
    }

    public void setPid2(Long pid2) {
        this.pid2 = pid2;
    }

    public void setWinner(Long winner) {
        this.winner = winner;
    }
}