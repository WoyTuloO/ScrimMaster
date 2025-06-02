package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.TeamDTO;
import com.woytuloo.ScrimMaster.DTO.TeamRequest;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Services.TeamService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(TeamController.class)
class TeamControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean TeamService teamService;
    @Autowired ObjectMapper objectMapper;

    @WithMockUser
    @Test void getTeams_byId_found() throws Exception {
        Mockito.when(teamService.getTeamById(5L)).thenReturn(Optional.of(new Team()));
        mockMvc.perform(get("/api/team?teamId=5")).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getTeams_byId_notFound() throws Exception {
        Mockito.when(teamService.getTeamById(6L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/team?teamId=6")).andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test void getTeams_byName_found() throws Exception {
        Team t = new Team();
        t.setTeamName("Avengers");
        Mockito.when(teamService.getTeamByName("Avengers")).thenReturn(Optional.of(t));
        mockMvc.perform(get("/api/team?name=Avengers")).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getTeams_byName_notFound() throws Exception {
        Mockito.when(teamService.getTeamByName("Nope")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/team?name=Nope")).andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test void getTeams_allTeams() throws Exception {
        Mockito.when(teamService.getAllTeams()).thenReturn(List.of(new Team()));
        mockMvc.perform(get("/api/team")).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getTeams_noneFound() throws Exception {
        Mockito.when(teamService.getAllTeams()).thenReturn(List.of());
        mockMvc.perform(get("/api/team")).andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test void addTeam() throws Exception {
        TeamDTO dto = TeamDTO.builder().teamId(1L).teamName("AA").build();
        Mockito.when(teamService.createTeam(any())).thenReturn(dto);
        TeamRequest req = new TeamRequest();
        req.setTeamName("AA");
        req.setCaptainId(1L);
        req.setPlayerIds(List.of(1L,2L));
        mockMvc.perform(post("/api/team").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @WithMockUser
    @Test void updateTeam_found() throws Exception {
        TeamDTO t = TeamDTO.builder()
                .teamId(2L)
                .teamName("AA")
                .teamRanking(0)
                .build();

        Mockito.when(teamService.updateTeam(any(TeamRequest.class))).thenReturn(t);

        TeamRequest req = new TeamRequest();
        req.setTeamName("AA");
        req.setCaptainId(2L);
        req.setPlayerIds(List.of(2L, 3L));

        mockMvc.perform(put("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk());
    }



    @WithMockUser
    @Test void updateTeam_notFound() throws Exception {
        Mockito.when(teamService.updateTeam((Team) any())).thenReturn(null);
        TeamRequest req = new TeamRequest();
        req.setTeamName("XX");
        mockMvc.perform(put("/api/team").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test void deleteTeam() throws Exception {
        mockMvc.perform(delete("/api/team/8").with(csrf())).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getCaptainsTeams() throws Exception {
        Mockito.when(teamService.getCaptainsTeams(11L)).thenReturn(List.of());
        mockMvc.perform(get("/api/team/11")).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getPlayerTeams_found() throws Exception {
        Mockito.when(teamService.getPlayerTeams()).thenReturn(List.of());
        mockMvc.perform(get("/api/team/me")).andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getPlayerTeams_notFound() throws Exception {
        Mockito.when(teamService.getPlayerTeams()).thenReturn(null);
        mockMvc.perform(get("/api/team/me")).andExpect(status().isNotFound());
    }
}
