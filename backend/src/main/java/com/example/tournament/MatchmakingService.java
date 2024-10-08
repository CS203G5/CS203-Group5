package com.example.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchmakingService {

    @Autowired
    private TournamentRepository tournamentRepository;

    // Temporary variables to hold settings during session
    private String matchmakingType;
    private String matchSchedule;

    public String setMatchmakingSettings(Long tournamentId, String matchmakingType, String matchSchedule) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Store settings in-memory (not persisted in a database)
        this.matchmakingType = matchmakingType;
        this.matchSchedule = matchSchedule;

        return "Matchmaking settings saved for Tournament ID: " + tournamentId;
    }

    // Random matchmaking logic based on streaks
    public String randomMatchmakingWithStreaks(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // logic here

        return "Matchmaking completed for Tournament ID: " + tournamentId + " with type " + this.matchmakingType;
    }

    public String getMatchmakingSettings() {
        return "Matchmaking Type: " + this.matchmakingType + ", Match Schedule: " + this.matchSchedule;
    }
}
