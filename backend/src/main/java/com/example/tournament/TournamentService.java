package com.example.tournament;

import java.util.List;
import java.util.Date;

public interface TournamentService {
    List<Tournament> findAll();
    Tournament findById(Long tid);
    List<Tournament> getOngoingTournaments();
    List<Tournament> getTournamentByOrganizer(Long aid);
    List<Tournament> fuzzySearchTournament(String searchTerm);
    List<Tournament> getTournamentByDate(Date startDate, Date endDate);
    List<Tournament> getTournamentBySorted(String sortBy, String order);
    List<Tournament> getTournamentByMatchingAlgo(Boolean isRandom);
    Tournament save(Tournament tournament);
    Tournament update(Long tid,Tournament tournament);
    void deleteById(List<Long> deleteList);
}