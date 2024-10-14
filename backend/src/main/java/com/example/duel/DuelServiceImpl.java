package com.example.duel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.example.tournament.TournamentNotFoundException;

@Transactional
@Service
public class DuelServiceImpl implements DuelService{

    @Autowired
    DuelRepository duelRepository;

    public List<Duel> findAll() {
        return duelRepository.findAll();
    }

    public List<Duel> getDuelsByTournament(Long tid) {
        if (!duelRepository.existsById(tid)) {
            throw new TournamentNotFoundException(tid);
        }
        return duelRepository.getDuelsByTournament(tid);
    }

    public List<Duel> getDuelsByRoundName(String roundName) {
        return duelRepository.getDuelsByRoundName(roundName);
    }
    
    public Duel getDuelById(Long did) {
        if (!duelRepository.existsById(did)) {
            throw new DuelNotFoundException(did);
        }
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
            duel.setPlayer1(newDuel.getPlayer1());
            duel.setPlayer2(newDuel.getPlayer2());
            duel.setRoundName(newDuel.getRoundName());
            return duelRepository.save(duel);
        }).orElseThrow(() -> new EntityNotFoundException("Duel not found with id: " + did));
    }

    public Duel updateDuelResult(Long did, DuelResult result) {
        return duelRepository.findById(did).map(duel -> {
            duel.setResult(result);
            if (result.getPlayer1Time() < result.getPlayer2Time()) {
                duel.setWinner(duel.getPlayer1().getProfileId());
            } else if (result.getPlayer1Time() > result.getPlayer2Time()) {
                duel.setWinner(duel.getPlayer2().getProfileId());
            } else {
                duel.setWinner(null);
            }
            return duelRepository.save(duel);
        }).orElseThrow(() -> new EntityNotFoundException("Duel not found with id: " + did));
    }

    public void deleteDuel(Long did) {
        if (!duelRepository.existsById(did)) {
            throw new DuelNotFoundException(did);
        }
        duelRepository.deleteById(did);
    }  
}
