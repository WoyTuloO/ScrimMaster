// src/main/java/com/woytuloo/ScrimMaster/Controllers/ChatRequestController.java
package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Records.ChatRequest;
import com.woytuloo.ScrimMaster.Repositories.ChatRequestStore;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatRequestController {

    private final SimpMessagingTemplate ws;
    private final ChatRequestStore store;
    private final ChatService chatService;

    @Autowired
    public ChatRequestController(SimpMessagingTemplate ws, ChatRequestStore store, ChatService chatService) {
        this.ws = ws;
        this.store = store;
        this.chatService = chatService;
    }

    @MessageMapping("/chat.request")
    public void request(ChatRequest rq, Principal principal) {

        ChatRequest request = new ChatRequest(
                principal.getName(),
                rq.to(),
                rq.correlationId()
        );
        store.save(request);

        ws.convertAndSendToUser(
                request.to(),
                "/queue/chat-requests",
                request
        );
    }

    @MessageMapping("/chat.accept")
    public void accept(ChatRequest ack, Principal principal) {
        store.get(ack.correlationId()).ifPresent(rq -> {

            chatService.createRoom(
                    rq.correlationId().toString(),
                    rq.from(),
                    rq.to()
            );

            ws.convertAndSendToUser(rq.from(), "/queue/chat-accepted", rq);
            ws.convertAndSendToUser(rq.to(),   "/queue/chat-accepted", rq);

            store.remove(ack.correlationId());
        });
    }

    @MessageMapping("/chat.reject")
    public void reject(ChatRequest rej, Principal principal) {
        UUID id = rej.correlationId();
        store.get(id).ifPresent(rq -> {
            ws.convertAndSendToUser(rq.from(), "/queue/chat-rejected", rq);
            store.remove(id);
        });
    }
}
