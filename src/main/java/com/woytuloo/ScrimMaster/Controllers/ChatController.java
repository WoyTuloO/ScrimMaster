package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import com.woytuloo.ScrimMaster.Services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@CrossOrigin("*")
@RequestMapping("api/chat/user")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUsersChats(@PathVariable String userId) {
        List<ChatRoom> usersChatRooms = chatService.getUsersChatRooms(userId).stream().filter(cr -> cr.getStatus().equals("Open") || cr.getStatus().equals("Rejected")).toList();
        if(usersChatRooms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usersChatRooms, HttpStatus.OK);

    }


}
