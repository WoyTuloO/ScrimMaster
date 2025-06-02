package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import com.woytuloo.ScrimMaster.Services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Pobierz czaty użytkownika",
            description = "Zwraca listę czatów, w których uczestniczy wskazany użytkownik. Status pokoju czatu musi być 'Open' lub 'Rejected'. Zwraca 404 jeśli brak czatów.",
            parameters = @Parameter(name = "userId", in = ParameterIn.PATH, required = true, description = "ID użytkownika"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista czatów użytkownika", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoom.class)))),
                    @ApiResponse(responseCode = "404", description = "Brak czatów")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUsersChats(@PathVariable String userId) {
        List<ChatRoom> usersChatRooms = chatService.getUsersChatRooms(userId).stream().filter(cr -> cr.getStatus().equals("Open") || cr.getStatus().equals("Rejected")).toList();
        if(usersChatRooms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usersChatRooms, HttpStatus.OK);

    }


}
