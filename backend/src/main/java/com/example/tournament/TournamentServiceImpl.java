package com.example.tournament;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class TournamentServiceImpl implements TournamentService {
    @Autowired
    TournamentRepository tournamentRepository;

    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public Tournament findById(Long tid) {
        return tournamentRepository.findById(tid).orElse(null);
    }

    public List<Tournament> getOngoingTournaments() {
        java.time.LocalDate today = java.time.LocalDate.now();
    
    return tournamentRepository.findAll().stream()
        .filter(t -> {
            java.time.LocalDate tournamentDate = t.getDate().toLocalDate(); 
            return tournamentDate.isBefore(today.plusDays(1)) && tournamentDate.isAfter(today.minusDays(1));
        })
        .collect(Collectors.toList());
    }

    public List<Tournament> getTournamentByOrganizer(Long aid) {
        return tournamentRepository.getTournamentByOrganizer(aid);
    }

    public List<Tournament> fuzzySearchTournament(String searchTerm) {
        return tournamentRepository.fuzzySearchTournament(searchTerm);
    }

    public List<Tournament> getTournamentByDate(Date startDate, Date endDate) {
        return tournamentRepository.getTournamentByDate(startDate, endDate);
    }

    public List<Tournament> getTournamentBySorted(String sortBy, String order) {
        return tournamentRepository.getTournamentBySorted(sortBy, order);
    }

    public List<Tournament> getTournamentByMatchingAlgo(Boolean isRandom) {
        return tournamentRepository.getTournamentByMatchingAlgo(isRandom);
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament update(Long tid, Tournament newTournamentInfo) {
        return tournamentRepository.findById(tid).map(tournament -> {
            // Update the fields
            tournament.setName(newTournamentInfo.getName());
            tournament.setIsRandom(newTournamentInfo.getIsRandom());
            tournament.setDate(newTournamentInfo.getDate());
            tournament.setTime(newTournamentInfo.getTime());
            tournament.setLocation(newTournamentInfo.getLocation());
            tournament.setOrganizer(newTournamentInfo.getOrganizer());
            tournament.setDescription(newTournamentInfo.getDescription());
            tournament.setModifiedAt(LocalDateTime.now());
            return tournamentRepository.save(tournament);
        }).orElseThrow(() -> new EntityNotFoundException("Tournament not found with id: " + tid));
    }

    public void deleteById(List<Long> deleteList) {
        for (Long tid : deleteList) {
            if (!tournamentRepository.existsById(tid)) {
                throw new TournamentNotFoundException("Tournament with id " + tid + " does not exist");
            }
            tournamentRepository.deleteById(tid);
        }
    }  
    
}
