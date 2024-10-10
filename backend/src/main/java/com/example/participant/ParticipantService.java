package com.example.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant getParticipantById(int userId) {
        return participantRepository.findById(userId).orElse(null);
    }

    public List<Participant> getParticipantsByTournamentId(int tournamentId) {
        return participantRepository.findByTournamentId(tournamentId);
    }

    public Participant saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    public Participant updateParticipant(int userId, Participant participantDetails) {
        Participant participant = participantRepository.findById(userId).orElse(null);
        if (participant == null) {
            return null;
        }
        participant.setTournamentId(participantDetails.getTournamentId());
        participant.setWin(participantDetails.getWin());
        participant.setLose(participantDetails.getLose());
        participant.setScore(participantDetails.getScore());
        return participantRepository.save(participant);
    }

    public void deleteParticipant(int userId) {
        participantRepository.deleteById(userId);
    }
}