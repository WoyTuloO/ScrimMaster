package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.TeamInvitationDTO;
import com.woytuloo.ScrimMaster.Services.TeamInvitationService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TeamInvitationController.class)
class TeamInvitationControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean TeamInvitationService invitationService;

    @Test
    @WithMockUser
    void getPendingInvitations() throws Exception {
        Mockito.when(invitationService.getPendingInvitationsForUser())
                .thenReturn(List.of(new TeamInvitationDTO(1L, "t", "c")));
        mockMvc.perform(get("/api/team/invitations/pending"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void acceptInvitation() throws Exception {
        mockMvc.perform(post("/api/team/invitations/5/accept").with(csrf()))
                .andExpect(status().isOk());
        Mockito.verify(invitationService).acceptInvitation(5L);
    }

    @Test
    @WithMockUser
    void declineInvitation() throws Exception {
        mockMvc.perform(post("/api/team/invitations/7/decline").with(csrf()))
                .andExpect(status().isOk());
        Mockito.verify(invitationService).declineInvitation(7L);
    }
}

