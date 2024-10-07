package com.example.duel;

import com.example.tournament.Tournament;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "duel")
@Data
public class Duel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long duel_id;
    private Long pid1;
    private Long pid2;
    private String roundName;
    private Long winner;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    public Tournament getTournament(){
        return tournament;
    }
}
