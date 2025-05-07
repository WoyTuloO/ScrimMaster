package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.DTO.TeamDTO;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Services.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired MockMvc      mockMvc;
    @MockBean  TeamService  teamService;
    @Autowired ObjectMapper mapper;

    private Team team(long id, String name) {
        Team team1 = new Team();
        team1.setTeamId(id);
        team1.setTeamName(name);
        return team1;
    }

    @Test @DisplayName("GET /api/team – zwraca listę TeamDTO")
    void getAllTeams() throws Exception {
        Mockito.when(teamService.getAllTeams())
                .thenReturn(List.of(team(1,"Alpha"), team(2, "Bravo")));

        mockMvc.perform(get("/api/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].teamName").value("Alpha"))
                .andExpect(jsonPath("$[1].teamName").value("Bravo"));
    }

    @Test @DisplayName("GET /api/team – 404 gdy brak drużyn")
    void getAllTeams_empty() throws Exception {
        Mockito.when(teamService.getAllTeams()).thenReturn(List.of());

        mockMvc.perform(get("/api/team"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeamById_found() throws Exception {
        Mockito.when(teamService.getTeamById(5L)).thenReturn(Optional.of(team(5,"Epsilon")));

        mockMvc.perform(get("/api/team").param("teamId","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Epsilon"));
    }

    @Test
    void getTeamById_notFound() throws Exception {
        Mockito.when(teamService.getTeamById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/team").param("teamId","5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeamByName_found() throws Exception {
        Mockito.when(teamService.getTeamByName("Bravo")).thenReturn(Optional.of(team(2,"Bravo")));

        mockMvc.perform(get("/api/team").param("name","Bravo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamName").value("Bravo"));
    }

    @Test
    void getTeamByName_notFound() throws Exception {
        Mockito.when(teamService.getTeamByName("Ghost")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/team").param("name","Ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTeam_created() throws Exception {
        Team incoming = team(0,"Zulu");
        Team saved    = team(99,"Zulu");

        Mockito.when(teamService.addTeam(any())).thenReturn(saved);

        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incoming)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value(99));
    }

    @Test
    void updateTeam_ok() throws Exception {
        Team updated = team(7,"Delta");

        Mockito.when(teamService.updateTeam(any())).thenReturn(updated);

        mockMvc.perform(put("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Delta"));
    }

    @Test
    void updateTeam_notFound() throws Exception {
        Mockito.when(teamService.updateTeam(any())).thenReturn(null);

        mockMvc.perform(put("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(team(0,"X"))))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteTeam_ok() throws Exception {
        mockMvc.perform(delete("/api/team/{id}", 4))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("deleted")));

        Mockito.verify(teamService).deleteTeam(eq(4L));
    }
}
