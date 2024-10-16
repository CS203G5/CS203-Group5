package com.example.duel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/duel")
public class DuelController {

    @Autowired
    DuelService ds;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping()
    public ResponseEntity<List<Duel>> getDuelsByTournament(@RequestParam(required = false) Long tid) {
        if (tid == null) {
            return ResponseEntity.ok(ds.findAll());
        } else {
            return ResponseEntity.ok(ds.getDuelsByTournament(tid));
        }
    }

    @GetMapping("/{did}")
    public ResponseEntity<Duel> getDuelById(@PathVariable Long did) {
        return ResponseEntity.ok(ds.getDuelById(did));
    }

    @GetMapping("/round")
    public ResponseEntity<List<Duel>> getDuelsByRoundName(@RequestParam(required = false) String roundName) {
        if (roundName == null) {
            return ResponseEntity.ok(ds.findAll());
        } else {
            return ResponseEntity.ok(ds.getDuelsByRoundName(roundName));
        }
    }

    @GetMapping("/player")
    public ResponseEntity<List<Duel>> getDuelsByPlayer(@RequestParam(required = false) Long pid) {
        if (pid == null) {
            return ResponseEntity.ok(ds.findAll());
        } else {
            return ResponseEntity.ok(ds.getDuelsByPlayer(pid));
        }
    }

    @PostMapping()
    public ResponseEntity<Duel> createDuel(@RequestBody Duel duel) {
        return ResponseEntity.ok(ds.createDuel(duel));
    }

    @PutMapping("/{did}")
    public ResponseEntity<Duel> updateDuel(@PathVariable Long did, @RequestBody Duel duel) {
        Duel updatedDuel = ds.updateDuel(did, duel);

        return ResponseEntity.ok(updatedDuel);
    }
    
    @PutMapping("/{did}/result")
    public ResponseEntity<Duel> updateDuelResult(@PathVariable Long did, @RequestBody DuelResult result) {
        Duel updatedDuel = ds.updateDuelResult(did, result);
        messagingTemplate.convertAndSend("/topic/duel" + did + "/score", updatedDuel);

        return ResponseEntity.ok(updatedDuel);
    }
    
    @DeleteMapping("/{did}")
    public void deleteDuel(@PathVariable Long did) {
        ds.deleteDuel(did);
    }
}
