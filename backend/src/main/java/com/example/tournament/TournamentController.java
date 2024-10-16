package com.example.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.sql.Date;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
    @Autowired
    TournamentService ts;

    @GetMapping()
    public List<Tournament> getAllTournament() {
        return ts.findAll();
    }

    @GetMapping("/{tid}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long tid) {
        Tournament tournament = ts.findById(tid);
        if (tournament == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if not found
        }
        return ResponseEntity.ok(tournament); // Return 200 and the tournament if found
    }

    @GetMapping("/organizer/{aid}")
    public List<Tournament> getTournamentByOrganizer(@PathVariable Long aid) {
        return ts.getTournamentByOrganizer(aid);
    }

    @GetMapping("/ongoing")
    public List<Tournament> getOngoingTournaments() {
        return ts.getOngoingTournaments();
    }

    @GetMapping("/search")
    public List<Tournament> fuzzySearchTournament(@RequestParam(required = false) String searchTerm) {
        if (searchTerm == null) {
            return ts.findAll();
        } else {
            return ts.fuzzySearchTournament(searchTerm);
        }
    }

    @GetMapping("/filter")
    public List<Tournament> getTournamentByDate(@RequestParam(required = false) Date startDate, @RequestParam(required = false) Date endDate) {
        if (startDate == null && endDate == null) {
            return ts.findAll();
        } else {
            return ts.getTournamentByDate(startDate, endDate);
        }
    }

    @GetMapping("/sorted")
    public List<Tournament> getTournamentBySorted(@RequestParam(required = false) String sortBy, @RequestParam(required = false) String order) {
        if (sortBy == null && order == null) {
            return ts.getTournamentBySorted("modified_at", "desc");
        } else {
            return ts.getTournamentBySorted(sortBy, order);
        }
    }

    @GetMapping("/matching")
    public List<Tournament> getTournamentByMatchingAlgo(@RequestParam(required = false) Boolean isRandom) {
        if (isRandom == null) {
            return ts.findAll();
        } else {
            return ts.getTournamentByMatchingAlgo(isRandom);
        }
    }
    

    @PostMapping()
    public Tournament addTournament(@Valid @RequestBody Tournament tournament) {
        tournament.setModifiedAt(LocalDateTime.now());
        return ts.save(tournament);
    }

    @PutMapping("/{tid}")
    public Tournament updateTournament(@PathVariable Long tid, @Valid @RequestBody Tournament tournament) {
        tournament.setModifiedAt(LocalDateTime.now());
        return ts.update(tid, tournament);
    }

    @DeleteMapping()
    public void deleteTournament(@Valid @RequestBody List<Long> deleteList) {
        ts.deleteById(deleteList);
    }
}