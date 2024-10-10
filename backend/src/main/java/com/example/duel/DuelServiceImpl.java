package com.example.duel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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

    public List<Duel> getDuelsByRoundName(String roundName) {
        return duelRepository.getDuelsByRoundName(roundName);
    }
    
    public Duel getDuelById(Long did) {
        return duelRepository.findById(did).orElse(null);
    }

    public List<Duel> getDuelsByPlayer(Long pid) {
        return duelRepository.getDuelsByPlayer(pid);
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
            return duelRepository.save(duel);
        }).orElseThrow(() -> new EntityNotFoundException("Duel not found with id: " + did));
    }

    public Duel updateDuelResult(Long did, DuelResult result) {
        return duelRepository.findById(did).map(duel -> {
            duel.setResult(result);
            if (result.getplayer1Time() > result.getplayer2Time()) {
                duel.setWinner(duel.getPid1());
            } else if (result.getplayer1Time() < result.getplayer2Time()) {
                duel.setWinner(duel.getPid2());
            } else {
                duel.setWinner(null);
            }
            return duelRepository.save(duel);
        }).orElseThrow(() -> new EntityNotFoundException("Duel not found with id: " + did));
    }

    public void deleteDuel(Long did) {
        try{
            duelRepository.deleteById(did);
        } catch (Exception e) {
            System.out.println("Duel not found with id: " + did);
        }
    }  
}
