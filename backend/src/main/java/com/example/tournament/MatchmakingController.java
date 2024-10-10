package com.example.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    @Autowired
    private MatchmakingService matchmakingService;

    // Endpoint to set matchmaking settings
    @PostMapping("/settings")
    public ResponseEntity<String> setMatchmakingSettings(
            @RequestParam Long tournamentId,
            @RequestParam String matchmakingType,
            @RequestParam String matchSchedule) {

        String result = matchmakingService.setMatchmakingSettings(tournamentId, matchmakingType, matchSchedule);
        return ResponseEntity.ok(result);
    }

    // Endpoint for random matchmaking (streak-based)
    @PostMapping("/random/streak-based")
    public ResponseEntity<String> randomMatchmakingWithStreaks(@RequestParam Long tournamentId) {
        String result = matchmakingService.randomMatchmakingWithStreaks(tournamentId);
        return ResponseEntity.ok(result);
    }

    // Endpoint to get matchmaking settings
    @GetMapping("/settings")
    public ResponseEntity<String> getMatchmakingSettings() {
        String settings = matchmakingService.getMatchmakingSettings();
        return ResponseEntity.ok(settings);
    }
}
