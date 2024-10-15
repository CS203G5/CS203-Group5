package com.example.duel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

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
        try {
            // Call the stored procedure that handles the validation logic
            duelRepository.createDuel(
                duel.getPlayer1().getProfileId(),
                duel.getPlayer2().getProfileId(),
                duel.getRoundName(),
                duel.getWinner(),
                duel.getTournament().getTournamentId()
            );
            return duel;
        } catch (DataAccessException e) {
            if (e.getCause() instanceof BadSqlGrammarException) {
                String sqlErrorMessage = e.getCause().getMessage();
                if (sqlErrorMessage.contains("Players must be different")) {
                    throw new DuelCreationException("Players must be different");
                } else if (sqlErrorMessage.contains("A duel with the same players, round, and tournament already exists")) {
                    throw new DuelCreationException("A duel with the same players, round, and tournament already exists");
                }
            }
            throw new DuelCreationException("Failed to create duel due to a database error", e);
        }
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
            throw new DuelNotFoundException(did);
        }
        duelRepository.deleteById(did);
    }  
}
