package com.example.tournament;

import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Procedure(name = "getTournamentByOrganizer")
    List<Tournament> getTournamentByOrganizer(@Param("aid") Long aid);

    @Procedure(name = "fuzzySearchTournament")
    List<Tournament> fuzzySearchTournament(@Param("p_search_term") String searchTerm);

    @Procedure(name = "getTournamentByDate")
    List<Tournament> getTournamentByDate(@Param("p_startDate") Date startDate, @Param("p_endDate") Date endDate);

    @Procedure(name = "getTournamentBySorted")
    List<Tournament> getTournamentBySorted(@Param("p_sortBy") String sortBy, @Param("p_order") String order);

    @Procedure(name = "getTournamentByMatchingAlgo")
    List<Tournament> getTournamentByMatchingAlgo(@Param("p_isRandom") Boolean isRandom);

    @Procedure(name = "deleteTournament")
    void deleteTournament(@Param("tid") Long tid); 

} 