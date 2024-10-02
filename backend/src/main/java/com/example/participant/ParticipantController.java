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

    private final ParticipantService participantService;
    private final TournamentService tournamentService;
    private final DuelService dualService;

    @Autowired
    public ParticipantController(ParticipantService participantService, TournamentService tournamentService, DuelService dualService) {
        this.participantService = participantService;
        this.tournamentService = tournamentService;
        this.dualService = dualService;
    }

    @GetMapping
    public List<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @GetMapping("/{userId}")
    public Participant getParticipantById(@PathVariable int userId) {
        Participant participant = participantService.getParticipantById(userId);
        if (participant == null) {
            throw new ParticipantNotFoundException(userId);
        }
        return participant;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/duel")
    public Duel createDuel(@RequestBody Duel duel) {
        return dualService.createDuel(duel);
    }

    @DeleteMapping("/{userId}")
    public void deleteParticipant(@PathVariable int userId) {
        try {
            participantService.deleteParticipant(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ParticipantNotFoundException(userId);
        }
    }
}