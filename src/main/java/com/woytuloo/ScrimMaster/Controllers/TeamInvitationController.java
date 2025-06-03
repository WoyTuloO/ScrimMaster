package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.TeamInvitationDTO;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.TeamInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/team/invitations")
@RequiredArgsConstructor
public class TeamInvitationController {

    private final TeamInvitationService invitationService;


    @Operation(
            summary = "Lista oczekujących zaproszeń do zespołu",
            description = "Zwraca wszystkie niezaakceptowane zaproszenia do zespołu dla bieżącego użytkownika.",
            responses = @ApiResponse(responseCode = "200", description = "Lista zaproszeń", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeamInvitationDTO.class))))
    )
    @GetMapping("/pending")
    public List<TeamInvitationDTO> getPendingInvitations() {
        return invitationService.getPendingInvitationsForUser();
    }

    @Operation(
            summary = "Akceptujzaproszenie do zespołu",
            description = "Zmienia status zaproszenia na zaakceptowane dla bieżącego użytkownika.",
            parameters = @Parameter(name = "invId", in = ParameterIn.PATH, required = true, description = "ID zaproszenia"),
            responses = @ApiResponse(responseCode = "200", description = "Zaproszenie zaktualizowane")
    )

    @PostMapping("/{invId}/accept")
    public void accept(@PathVariable Long invId) {
        invitationService.acceptInvitation(invId);
    }

    @Operation(
            summary = "Odrzuć zaproszenie do zespołu",
            description = "Zmienia status zaproszenia na odrzucone dla bieżącego użytkownika.",
            parameters = @Parameter(name = "invId", in = ParameterIn.PATH, required = true, description = "ID zaproszenia"),
            responses = @ApiResponse(responseCode = "200", description = "Zaproszenie zaktualizowane")
    )
    @PostMapping("/{invId}/decline")
    public void decline(@PathVariable Long invId) {
        invitationService.declineInvitation(invId);
    }
}

