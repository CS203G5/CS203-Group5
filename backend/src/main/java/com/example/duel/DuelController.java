    package com.example.duel;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.tournament.TournamentNotFoundException;
=======
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.web.bind.annotation.*;

    import jakarta.validation.Valid;

    import java.util.List;
>>>>>>> dev/tests2

import java.util.List;

    @RestController
    @RequestMapping("/api/duel")
    public class DuelController {

        @Autowired
        DuelService ds;

        @Autowired
        private SimpMessagingTemplate messagingTemplate;
        
        @GetMapping()
        public List<Duel> getDuelsByTournament(@RequestParam(required = false) Long tid) {
            if (tid == null) {
                return ds.findAll();
            } else {
                return ds.getDuelsByTournament(tid);
            }
        }

        @GetMapping("/{did}")
        public Duel getDuelById(@PathVariable Long did) {
            return ds.getDuelById(did);
        }

        @GetMapping("/round")
        public List<Duel> getDuelsByRoundName(@RequestParam(required = false) String roundName) {
            if (roundName == null) {
                return ds.findAll();
            } else {
                return ds.getDuelsByRoundName(roundName);
            }
        }

        @GetMapping("/player")
        public List<Duel> getDuelsByPlayer(@RequestParam(required = false) Long pid) {
            if (pid == null) {
                return ds.findAll();
            } else {
                return ds.getDuelsByPlayer(pid);
            }
        }

        @PostMapping()
        public Duel createDuel(@Valid @RequestBody Duel duel) {
            return ds.createDuel(duel);
        }

        @PutMapping("/{did}")
        public Duel updateDuel(@PathVariable Long did, @RequestBody Duel duel) {
            Duel updatedDuel = ds.updateDuel(did, duel);

            return updatedDuel;
        }
        
        @PutMapping("/{did}/result")
        public Duel updateDuelResult(@PathVariable Long did, @RequestBody DuelResult result) {
            Duel updatedDuel = ds.updateDuelResult(did, result);
            messagingTemplate.convertAndSend("/topic/duel" + did + "/score", updatedDuel);

            return updatedDuel;
        }
        
        @DeleteMapping("/{did}")
        public void deleteDuel(@PathVariable Long did) {
            ds.deleteDuel(did);
        }

        List<Duel> duels = ds.getDuelsByTournament(tid);

        if (duels.isEmpty()) {
            throw new TournamentNotFoundException(tid);
        }

        return ResponseEntity.ok(duels);
    }
<<<<<<< HEAD

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
    public ResponseEntity<String> createDuel(@RequestBody Duel duel) {
        // Call the service method to create the duel
        String message = ds.createDuel(duel);

        // Check the returned message and set the appropriate HTTP status code
        if (message.equals("Duel created successfully")) {
            // Return 201 Created for successful creation
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } else if (message.equals("pid1 and pid2 cannot be the same") ||
                message.equals("An exact pairing with the same round_name already exists")) {
            // Return 409 Conflict for specific error messages
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            // Return 400 Bad Request for other types of errors
            return ResponseEntity.badRequest().body(message);
        }
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
=======
>>>>>>> dev/tests2
