package com.example.duel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class DuelServiceImpl implements DuelService{

    @Autowired
    DuelRepository duelRepository;

    public List<Duel> findAll() {
        return duelRepository.findAll();
    }

    public List<Duel> getDuelsByTournament(Long tid) {
        return duelRepository.getDuelsByTournament(tid);
    }

    public Duel getDuelById(Long did) {
        return duelRepository.findById(did).orElse(null);
    }

    public List<Duel> getByPlayer(Long pid) {
        return duelRepository.getByPlayer(pid);
    }

    public Duel createDuel(Duel duel) {
        return duelRepository.save(duel);
    }

    public Duel updateDuel(Long did, Duel newDuel) {
        // duelRepository.updateDuel(did, duel.getPid1(), duel.getPid2(), duel.getRoundName(), duel.getWinner());

        return duelRepository.findById(did).map(duel -> {
            duel.setPid1(newDuel.getPid1());
            duel.setPid2(newDuel.getPid2());
            duel.setRoundName(newDuel.getRoundName());
            duel.setWinner(newDuel.getWinner());
            return duelRepository.save(duel);
        }).orElse(null);
    }

    public void deleteDuel(Long did) {
        duelRepository.deleteDuel(did);
    }    
}
