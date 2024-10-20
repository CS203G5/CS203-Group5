package com.example.duel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class DuelServiceImpl implements DuelService {

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
        return duelRepository.findById(did).orElseThrow(() -> new DuelNotFoundException("Duel not found with id: " + did));
    }    

    public List<Duel> getDuelsByPlayer(Long pid) {
        return duelRepository.getDuelsByPlayer(pid);
    }

    public String createDuel(Duel duel) {
        String message = duelRepository.createDuel(
            duel.getTournament().getTournamentId(),
            duel.getRoundName(),
            duel.getPid1().getProfileId(),
            duel.getPid2().getProfileId(),
            duel.getWinner()
        );

        return message;
    }

    @Override
    public Duel updateDuel(Long did, Duel newDuelInfo) {
        return duelRepository.findById(did).map(duel -> {
            duel.setPid1(newDuelInfo.getPid1());
            duel.setPid2(newDuelInfo.getPid2());
            duel.setRoundName(newDuelInfo.getRoundName());
            return duelRepository.save(duel);
        }).orElseThrow(() -> new DuelNotFoundException("Duel not found with id: " + did));
    }
    
    public Duel updateDuelResult(Long did, DuelResult result) {
        return duelRepository.findById(did).map(duel -> {
            duel.setResult(result);
            if (result.getWinnerId() != null) {
                duel.setWinner(result.getWinnerId());
            } else {
                duel.setWinner(null);
            }
            return duelRepository.save(duel);
        }).orElseThrow(() -> new EntityNotFoundException("Duel not found with id: " + did));
    }
    
    public void deleteDuel(Long did) {
        if (!duelRepository.existsById(did)) {
            throw new DuelNotFoundException("Duel not found with id: " + did);
        }
        duelRepository.deleteById(did);
    }
    
}
