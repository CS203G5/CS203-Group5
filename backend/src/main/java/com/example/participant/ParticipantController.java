package com.example.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.duel.Duel;
import com.example.duel.DuelService;
import com.example.tournament.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    ParticipantService participantService;
    TournamentService tournamentService;
    DuelService duelService;

    @Autowired
    public ParticipantController(ParticipantService participantService, TournamentService tournamentService, DuelService duelService) {
        this.participantService = participantService;
        this.tournamentService = tournamentService;
        this.duelService = duelService;
    }
    
    @GetMapping
    public List<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @GetMapping("/tournament/{tournament_id}")
    public List<Participant> getParticipantsByTournamentId(@PathVariable Long tournament_id) {
        List<Participant> participants = participantService.getParticipantsByTournamentId(tournament_id);
        
        if (participants.isEmpty()) {
            throw new ParticipantNotFoundException(tournament_id);
        }
        
        return participants;
    }

    @GetMapping("/user/{userId}")
    public List<Participant> getParticipantsByUserId(@PathVariable Long user_id) {
        // return participantService.getParticipantsByUserId(userId);
        return participantService.getParticipantsByUserId(user_id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public Participant registerParticipant(@RequestBody Participant participant) {
        return participantService.saveParticipant(participant);
    }

    @DeleteMapping("/user/{user_id}/tournament/{tournament_id}")
    public void deleteParticipant(@PathVariable Long user_id, @PathVariable Long tournament_id) {
        ParticipantId participantId = new ParticipantId(tournament_id, user_id);
        
        if (!participantService.existsById(participantId)) {
            throw new ParticipantNotFoundException(user_id, tournament_id);
        }
    
        participantService.deleteById(participantId);
    }
    
}