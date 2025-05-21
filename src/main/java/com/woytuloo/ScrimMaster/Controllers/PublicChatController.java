// src/main/java/com/woytuloo/ScrimMaster/Controllers/PublicChatController.java
package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Records.ChatMessage;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/public")
public class PublicChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate ws;

    public PublicChatController(ChatService chatService, SimpMessagingTemplate ws) {
        this.chatService = chatService;
        this.ws = ws;
    }

    @GetMapping
    public List<ChatMessage> history(@RequestParam(defaultValue="100") int limit) {
        return chatService.getLatestPublic(limit).stream()
                .map(m -> new ChatMessage("CHAT", m.getContent(), m.getSender()))
                .collect(Collectors.toList());
    }

    @MessageMapping("/chat.public")
    public void handlePublic(ChatMessage msg, Principal p) {
        String sender = p.getName();
        chatService.savePublic(sender, msg.content());
        ws.convertAndSend("/topic/public", new ChatMessage("CHAT", msg.content(), sender));
    }
}
