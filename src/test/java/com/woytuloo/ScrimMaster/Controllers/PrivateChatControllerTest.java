package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Services.ChatService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(PrivateChatController.class)
class PrivateChatControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ChatService chatService;
    @MockBean SimpMessagingTemplate ws;

    @Test void getHistory_ok() throws Exception {
        Mockito.when(chatService.isParticipant(anyString(), anyString())).thenReturn(true);
        Mockito.when(chatService.getLatestPrivate(anyString(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/api/chat/private/room1").with(user("someUser")))
                .andExpect(status().isOk());
    }

    @Test void getHistory_forbidden() throws Exception {
        Mockito.when(chatService.isParticipant(anyString(), anyString())).thenReturn(false);
        mockMvc.perform(get("/api/chat/private/room1")
                        .with(user("someUser")))
                .andExpect(status().isForbidden());
    }
}
