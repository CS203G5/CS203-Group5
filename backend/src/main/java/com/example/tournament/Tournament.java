package com.example.tournament;

import com.example.duel.Duel;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.time.LocalDateTime;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "tournament")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tournament {
    @Column(name = "tournament_id")
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO) 
    private Long tournament_id;

    @Column(name = "name")
    @NotNull 
    private String name;

    @Column(name = "is_random")
    @JsonProperty("is_random")
    @NotNull
    private boolean is_random;

    @Column(name = "date")
    private Date date;

    @Column(name = "time")
    private Time time;

    @Column(name = "location")
    private String location;

    @Column(name = "organizer_id")
    private Long organizer_id;

    @Column(name = "description")
    @Size(max = 255) 
    private String description;

    @Column(name = "modified_at")
    private LocalDateTime modified_at = LocalDateTime.now(); // Default to current time

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true) 
    @JsonIgnore
    private List<Duel> duels;

    public Tournament(String name, boolean is_random, Date date, Time time, String location, Long organizer_id, String description) {
        this.name = name;
        this.is_random = is_random;
        this.date = date;
        this.time = time;
        this.location = location;
        this.organizer_id = organizer_id;
        this.description = description;
    }

    public Long getTournamentId() {
        return tournament_id;
    }
    
    public boolean getIsRandom() {
        return is_random;
    }

    public void setIsRandom(boolean is_random) {
        this.is_random = is_random;
    }

    public Long getOrganizer() {
        return organizer_id;
    }

    public void setOrganizer(Long organizer_id) {
        this.organizer_id = organizer_id;
    }

    public Long getTournamentId(Long tournament_id){
        return tournament_id;
    }

    public void setTournamentId (Long tournament_id){
        this.tournament_id = tournament_id;
    }

    public String setModifiedAt(){
        return modified_at.toString();
    }

    public void setModifiedAt(LocalDateTime modified_at){
        this.modified_at = modified_at;
    }
}