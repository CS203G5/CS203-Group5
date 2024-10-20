package com.example.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    @Transactional
    public List<Participant> getParticipantsByUserId(Long user_id) {
        return participantRepository.getParticipantsByUserId(user_id);
    }

    @Transactional
    public List<Participant> getParticipantsByTournamentId(Long tournament_id) {
        return participantRepository.getParticipantsByTournamentId(tournament_id);
    }

    public void deleteById(ParticipantId participantId) {
        // Check if the participant exists before trying to delete
        boolean exists = participantRepository.existsById(participantId);
        if (!exists) {
            throw new ParticipantNotFoundException(participantId.getProfile(), participantId.getTournament());
        }
        participantRepository.deleteById(participantId);
    }
}