    package com.example.duel;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.web.bind.annotation.*;

    import jakarta.validation.Valid;

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
    }
