package com.example.unit.duel;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.duel.Duel;
import com.example.duel.DuelRepository;
import com.example.duel.DuelServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DuelServiceTest {

    @Mock
    DuelRepository duelRepository;

    @InjectMocks
    DuelServiceImpl duelService;

@Test
public void testGetDuelsByTournament() {
    // Given
    Long tournamentId = 1L;

    // Create Duel objects using no-arg constructor and setters
    Duel duel1 = new Duel();
    duel1.setPid1(1L);
    duel1.setPid2(2L);
    duel1.setRoundName("Round 1");
    duel1.setWinner(1L);

    Duel duel2 = new Duel();
    duel2.setPid1(3L);
    duel2.setPid2(4L);
    duel2.setRoundName("Round 2");
    duel2.setWinner(3L);

    List<Duel> mockDuels = Arrays.asList(duel1, duel2);
    when(duelRepository.getDuelsByTournament(tournamentId)).thenReturn(mockDuels);

    // When
    List<Duel> result = duelService.getDuelsByTournament(tournamentId);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(duelRepository).getDuelsByTournament(tournamentId);
}

}
