package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Records.ChatRequest;
import com.woytuloo.ScrimMaster.Repositories.ChatRequestStore;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ChatRequestControllerTest {
    @Mock SimpMessagingTemplate ws;
    @Mock ChatRequestStore store;
    @Mock ChatService chatService;
    @Mock Principal principal;

    @InjectMocks ChatRequestController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn("fromUser");
    }

    @Test
    void request_savesAndSends() {
        UUID id = UUID.randomUUID();
        ChatRequest input = new ChatRequest("irrelevant", "toUser", id);

        controller.request(input, principal);

        ArgumentCaptor<ChatRequest> captor = ArgumentCaptor.forClass(ChatRequest.class);
        verify(store).save(captor.capture());
        ChatRequest saved = captor.getValue();
        verify(ws).convertAndSendToUser(eq("toUser"), eq("/queue/chat-requests"), eq(saved));
    }

    @Test
    void accept_found_stores_and_notifies_and_removes() {
        UUID id = UUID.randomUUID();
        ChatRequest rq = new ChatRequest("fromUser", "toUser", id);

        when(store.get(id)).thenReturn(Optional.of(rq));
        ChatRequest ack = new ChatRequest("fromUser", "toUser", id);

        controller.accept(ack, principal);

        verify(chatService).createRoom(id.toString(), "fromUser", "toUser");
        verify(ws).convertAndSendToUser("fromUser", "/queue/chat-accepted", rq);
        verify(ws).convertAndSendToUser("toUser", "/queue/chat-accepted", rq);
        verify(store).remove(id);
    }

    @Test
    void accept_notFound_nothingHappens() {
        UUID id = UUID.randomUUID();
        when(store.get(id)).thenReturn(Optional.empty());
        ChatRequest ack = new ChatRequest("fromUser", "toUser", id);

        controller.accept(ack, principal);

        verifyNoInteractions(chatService);
        verify(store, never()).remove(any());
        verify(ws, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    void reject_found_notifiesAndRemoves() {
        UUID id = UUID.randomUUID();
        ChatRequest rq = new ChatRequest("fromUser", "toUser", id);
        when(store.get(id)).thenReturn(Optional.of(rq));

        ChatRequest rej = new ChatRequest("fromUser", "toUser", id);

        controller.reject(rej, principal);

        verify(ws).convertAndSendToUser("fromUser", "/queue/chat-rejected", rq);
        verify(store).remove(id);
    }

    @Test
    void reject_notFound_nothingHappens() {
        UUID id = UUID.randomUUID();
        when(store.get(id)).thenReturn(Optional.empty());
        ChatRequest rej = new ChatRequest("fromUser", "toUser", id);

        controller.reject(rej, principal);

        verify(ws, never()).convertAndSendToUser(any(), any(), any());
        verify(store, never()).remove(any());
    }
}
