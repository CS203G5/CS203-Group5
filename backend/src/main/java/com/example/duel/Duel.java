package com.example.duel;

import com.example.tournament.Tournament;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
}
