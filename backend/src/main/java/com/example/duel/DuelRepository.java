package com.example.duel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DuelRepository extends JpaRepository<Duel, Long> {        
    @Procedure(name = "getDuelsByTournament")
    List<Duel> getDuelsByTournament(@Param("tid") Long tid);

    @Procedure(name = "getDuelsByRoundName")
    List<Duel> getDuelsByRoundName(@Param("roundName") String roundName);

    @Procedure(name = "getDuelsByPlayer")
    List<Duel> getDuelsByPlayer(@Param("p_pid") Long pid);

    @Procedure(name = "deleteDuel")
    void deleteDuel(@Param("did") Long did); 
}

