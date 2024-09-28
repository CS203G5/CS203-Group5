package com.example.tournament;

import java.util.List;

import com.example.duel.Duel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long tournament_id;
    @NotNull private String name;
    @JsonProperty("isRandom") private boolean isRandom;
    private Date date;
    private Time time;
    private String location;
    private Long organizer_id;
    @Size(max = 255) private String description;
    private LocalDateTime modifiedAt = LocalDateTime.now(); // Default to current time

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true) 
    @JsonIgnore
    private List<Duel> duels;

    public boolean getIsRandom() {
        return isRandom;
    }

    public void setIsRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    public Long getOrganizer() {
        return organizer_id;
    }

    public void setOrganizer(Long organizer_id) {
        this.organizer_id = organizer_id;
    }
}