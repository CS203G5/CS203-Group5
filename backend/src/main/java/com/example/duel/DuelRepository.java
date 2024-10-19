package com.example.duel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DuelRepository extends JpaRepository<Duel, Long> {

    @Procedure(name = "getDuelsByTournament")
    List<Duel> getDuelsByTournament(@Param("p_tid") Long tid);

    @Procedure(name = "getDuelsByRoundName")
    List<Duel> getDuelsByRoundName(@Param("p_round_name") String roundName);

    @Procedure(name = "getDuelsByPlayer")
    List<Duel> getDuelsByPlayer(@Param("p_pid") Long pid);
    
    @Procedure(name="createDuel")
    Duel createDuel(@Param("p_pid1") Long pid1, @Param("p_pid2") Long pid2, @Param("p_round_name") String roundName, @Param("p_winner") Long winner, @Param("p_tid") Long tid);
    
    @Procedure(name = "deleteDuel")
    void deleteDuel(@Param("p_did") Long did); 
}

