package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Services.ChatService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicChatController.class)
class PublicChatControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ChatService chatService;
    @MockBean SimpMessagingTemplate ws;

    @WithMockUser
    @Test void history() throws Exception {
        Mockito.when(chatService.getLatestPublic(anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/api/chat/public"))
                .andExpect(status().isOk());
    }
}
