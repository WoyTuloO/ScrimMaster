package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import com.woytuloo.ScrimMaster.Records.ChatMessage;
import com.woytuloo.ScrimMaster.Services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/private")
@CrossOrigin(origins = "*")
public class PrivateChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public PrivateChatController(ChatService chatService,
                                 SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }


    @Operation(
            summary = "Pobierz historię prywatnego czatu",
            description = "Zwraca listę ostatnich wiadomości z prywatnego pokoju czatu. Użytkownik musi być uczestnikiem pokoju. Limit domyślnie 100.",
            parameters = {
                    @Parameter(name = "roomId", in = ParameterIn.PATH, required = true, description = "ID pokoju czatu"),
                    @Parameter(name = "limit", in = ParameterIn.QUERY, required = false, description = "Limit wiadomości (domyślnie 100)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista wiadomości czatu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class)))),
                    @ApiResponse(responseCode = "403", description = "Brak dostępu do tego pokoju")
            }
    )
    @GetMapping("/{roomId}")
    public List<ChatMessage> getHistory(
            @PathVariable String roomId,
            Principal principal,
            @RequestParam(defaultValue = "100") int limit
    ) {
        String user = principal.getName();
        if (!chatService.isParticipant(roomId, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Nie masz dostępu do tego czatu"
            );
        }

        return chatService.getLatestPrivate(roomId, limit).stream()
                .map(m -> new ChatMessage("CHAT", m.getContent(), m.getSender()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list;})
                );
    }


    @MessageMapping("/chat.private.{roomId}")
    public void handlePrivate(
            @DestinationVariable String roomId,
            ChatMessage msg,
            Principal principal
    ) {
        String sender = principal.getName();
        if (!chatService.isParticipant(roomId, sender)) {
            return;
        }
        chatService.savePrivate(roomId, sender, msg.content());

        Optional<ChatRoom> roomOpt = chatService.getChatRoomById(roomId).stream().findFirst();

        if(roomOpt.isEmpty())
            return;

        ChatRoom room = roomOpt.get();

        ChatMessage out = new ChatMessage("CHAT", msg.content(), sender);
        messagingTemplate.convertAndSendToUser(
                room.getUserA(), "/queue/private." + roomId, out);
        messagingTemplate.convertAndSendToUser(
                room.getUserB(), "/queue/private." + roomId, out);
    }

}