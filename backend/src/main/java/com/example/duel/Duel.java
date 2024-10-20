package com.example.duel;

import com.example.tournament.Tournament;
import com.example.profile.Profile;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "duel")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Duel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long duel_id;

    private String round_name;

    @Embedded
    private DuelResult result; // Ensure this class is defined properly

    private Long winner;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pid1")
    private Profile pid1;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pid2")
    private Profile pid2;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    public Long getDuelId(){
        return duel_id;
    }

    public void setTournament(Tournament tournament){
        this.tournament = tournament;
    }

    public Tournament getTournament(){
        return tournament;
    }

    public void setWinner(Long winner){
        this.winner = winner;
    }

    public String getRoundName(){
        return round_name;
    }

    public void setRoundName(String round_name){
        this.round_name = round_name;
    }

    public void setPid1(Profile pid1) {
        this.pid1 = pid1;
    }

    public void setPid2(Profile pid2) {
        this.pid2 = pid2;
    }
}
