package com.example.tournament;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matchmaking_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchmakingSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private String matchmakingType;  // random or streak

    private String matchSchedule;  // string to represent the schedule for matches
}
