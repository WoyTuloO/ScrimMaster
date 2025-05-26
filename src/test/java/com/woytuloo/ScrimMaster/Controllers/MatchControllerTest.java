package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.DTO.MatchRequest;
import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Services.MatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    MatchService matchService;
    @Autowired
    ObjectMapper mapper;


    private Match sample(long id) {
        Match m = new Match();
        m.setId(id);
        m.setTeam1Score(13);
        m.setTeam2Score(16);
        Team team1 = new Team();
        team1.setTeamId(1L);
        team1.setTeamName("Team A");

        Team team2 = new Team();
        team2.setTeamId(2L);
        team2.setTeamName("Team B");

        m.setTeams(List.of(team1, team2));
        return m;
    }


    @Test
    @DisplayName("GET /api/match – lista wszystkich meczów")
    void getAllMatches() throws Exception {
        Mockito.when(matchService.getAllMatches())
                .thenReturn(List.of(sample(1), sample(2)));

        mockMvc.perform(get("/api/match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getMatchById() throws Exception {
        Mockito.when(matchService.getMatchById(10L)).thenReturn(Optional.of(sample(10)));

        mockMvc.perform(get("/api/match/{id}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getTeamMatches() throws Exception {
        Mockito.when(matchService.getTeamMatches(5L))
                .thenReturn(List.of(sample(3)));

        mockMvc.perform(get("/api/match/team/{teamId}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void addMatch() throws Exception {
        Match toSave  = sample(0);
        Match saved   = sample(99);

        Mockito.when(matchService.addMatch(any(Match.class))).thenReturn(saved);

        mockMvc.perform(post("/api/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(toSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99));
    }

    @Test
    void updateMatch() throws Exception {
        Match incoming = sample(42);

        Mockito.doNothing().when(matchService).updateMatch(any(MatchRequest.class));

        mockMvc.perform(put("/api/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void deleteMatch() throws Exception {
        Mockito.when(matchService.deleteMatch(7)).thenReturn(1L);

        mockMvc.perform(delete("/api/match/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("deleted")));

        Mockito.verify(matchService).deleteMatch(eq(7L));
    }
}
