package com.example.participant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    List<Participant> findByTournamentId(int tournamentId);
}