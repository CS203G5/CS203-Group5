package com.example.duel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/duel")
public class DuelController {

    @Autowired
    DuelService ds;
    
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

    @GetMapping("/player")
    public List<Duel> getByPlayer(@RequestParam(required = false) Long pid) {
        if (pid == null) {
            return ds.findAll();
        } else {
            return ds.getByPlayer(pid);
        }
    }

    @PostMapping()
    public Duel createDuel(@RequestBody Duel duel) {
        return ds.createDuel(duel);
    }

    @PutMapping("/{did}")
    public Duel updateDuel(@PathVariable Long did, @RequestBody Duel duel) {
        return ds.updateDuel(did, duel);
    }
    
    @DeleteMapping("/{did}")
    public void deleteDuel(@PathVariable Long did) {
        ds.deleteDuel(did);
    }
}
