package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import com.woytuloo.ScrimMaster.Records.ChatMessage;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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

    @GetMapping("/{roomId}")
    public List<ChatMessage> getHistory(
            @PathVariable String roomId,
            Principal principal,
            @RequestParam(defaultValue = "100") int limit
    ) {
//        return List.of(
//                new ChatMessage("CHAT", "Testowa wiadomość #1", "aaa"),
//                new ChatMessage("CHAT", "Testowa wiadomość #2", "ddd")
//        );
//
        String user = principal.getName();
        if (!chatService.isParticipant(roomId, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Nie masz dostępu do tego czatu"
            );
        }

        System.out.println(chatService.getLatestPrivate(roomId, limit).stream()
                .map(m -> new ChatMessage("CHAT", m.getContent(), m.getSender()))
                .collect(Collectors.toList()));

        return chatService.getLatestPrivate(roomId, limit).stream()
                .map(m -> new ChatMessage("CHAT", m.getContent(), m.getSender()))
                .collect(Collectors.toList());
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