package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Records.ChatMessage;
import com.woytuloo.ScrimMaster.Services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
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

    @Operation(
            summary = "Historia czatu publicznego",
            description = "Zwraca listę ostatnich wiadomości z czatu publicznego. Limit domyślnie 100.",
            parameters = @Parameter(name = "limit", in = ParameterIn.QUERY, required = false, description = "Limit wiadomości (domyślnie 100)"),
            responses = @ApiResponse(responseCode = "200", description = "Lista wiadomości", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class))))
    )
    @GetMapping
    public List<ChatMessage> history(@RequestParam(defaultValue="100") int limit) {
        return chatService.getLatestPublic(limit).stream()
                .map(m -> new ChatMessage("CHAT", m.getContent(), m.getSender()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list;})
                );
    }

    @MessageMapping("/chat.public")
    public void handlePublic(ChatMessage msg, Principal p) {
        String sender = p.getName();
        chatService.savePublic(sender, msg.content());
        ws.convertAndSend("/topic/public", new ChatMessage("CHAT", msg.content(), sender));
    }
}
