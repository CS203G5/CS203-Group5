package com.example.duel;

import java.util.List;

public interface DuelService {
    List<Duel> findAll();
    List<Duel> getDuelsByTournament(Long tid);
    List<Duel> getByPlayer(Long pid);
    Duel getDuelById(Long did);
    Duel createDuel(Duel duel);
    Duel updateDuel(Long did, Duel duel);
    void deleteDuel(Long did);    
}