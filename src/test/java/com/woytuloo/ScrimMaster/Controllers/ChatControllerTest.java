package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(ChatController.class)
class ChatControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ChatService chatService;

    @WithMockUser
    @Test void getUsersChats_found() throws Exception {
        ChatRoom room = new ChatRoom("id","a","b");
        room.setStatus("Open");
        Mockito.when(chatService.getUsersChatRooms(anyString())).thenReturn(List.of(room));
        mockMvc.perform(get("/api/chat/user/abc"))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test void getUsersChats_notFound() throws Exception {
        Mockito.when(chatService.getUsersChatRooms(anyString())).thenReturn(List.of());
        mockMvc.perform(get("/api/chat/user/abc"))
                .andExpect(status().isNotFound());
    }
}
