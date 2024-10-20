package com.example.duel;

import java.util.List;

public interface DuelService {
    List<Duel> findAll();
    List<Duel> getDuelsByTournament(Long tid);
    List<Duel> getDuelsByRoundName(String roundName);
    List<Duel> getDuelsByPlayer(Long pid);
    Duel getDuelById(Long did);
    String createDuel(Duel duel);
    Duel updateDuel(Long did, Duel duel);
    Duel updateDuelResult(Long did, DuelResult result);
    void deleteDuel(Long did);  
}