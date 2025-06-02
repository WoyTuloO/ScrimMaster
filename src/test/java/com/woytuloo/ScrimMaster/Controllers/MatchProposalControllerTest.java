package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.DTO.MatchProposalDTO;
import com.woytuloo.ScrimMaster.DTO.MatchProposalRequest;
import com.woytuloo.ScrimMaster.Models.ProposalStatus;
import com.woytuloo.ScrimMaster.Services.MatchProposalService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(MatchProposalController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchProposalControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean MatchProposalService matchProposalService;
    @Autowired ObjectMapper objectMapper;


    @Test
    void addProposal_ok() throws Exception {
        MatchProposalRequest req = new MatchProposalRequest();
        Mockito.when(matchProposalService.addProposal(any())).thenReturn(ProposalStatus.Pending);
        mockMvc.perform(post("/api/match/proposal/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void addProposal_exception() throws Exception {
        MatchProposalRequest req = new MatchProposalRequest();
        Mockito.when(matchProposalService.addProposal(any())).thenThrow(new RuntimeException("error"));
        mockMvc.perform(post("/api/match/proposal/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUsersProposals_found() throws Exception {
        Mockito.when(matchProposalService.getUsersProposals(6L)).thenReturn(List.of(new MatchProposalDTO()));
        mockMvc.perform(get("/api/match/proposal/user/6"))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersProposals_bad() throws Exception {
        Mockito.when(matchProposalService.getUsersProposals(7L)).thenReturn(null);
        mockMvc.perform(get("/api/match/proposal/user/7"))
                .andExpect(status().isBadRequest());
    }
}
