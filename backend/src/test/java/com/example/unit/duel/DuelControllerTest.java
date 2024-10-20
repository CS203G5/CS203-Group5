package com.example.unit.duel;

import com.example.duel.Duel;
import com.example.duel.DuelService;
import com.example.duel.DuelController;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DuelControllerTest {

    @Mock
    private DuelService duelService;

    @InjectMocks
    private DuelController duelController;

    private MockMvc mockMvc;

    public DuelControllerTest() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(duelController).build();
    }

    @Test
    void testGetDuelsByTournament() throws Exception {
        List<Duel> duels = Arrays.asList(new Duel(), new Duel());
        when(duelService.getDuelsByTournament(1L)).thenReturn(duels);

        mockMvc.perform(get("/api/duel")
                .param("tid", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(duelService, times(1)).getDuelsByTournament(1L);
    }

    @Test
    void testUpdateDuel() throws Exception {
        Duel duel = new Duel();
        when(duelService.updateDuel(eq(1L), any(Duel.class))).thenReturn(duel);

        mockMvc.perform(put("/api/duel/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(duel)))
                .andExpect(status().isOk());

        verify(duelService, times(1)).updateDuel(eq(1L), any(Duel.class));
    }

    @Test
    void testDeleteDuel() throws Exception {
        mockMvc.perform(delete("/api/duel/1"))
                .andExpect(status().isOk());

        verify(duelService, times(1)).deleteDuel(1L);
    }
}
