package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.TeamInvitationDTO;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.TeamInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/team/invitations")
@RequiredArgsConstructor
public class TeamInvitationController {

    private final TeamInvitationService invitationService;

    @GetMapping("/pending")
    public List<TeamInvitationDTO> getPendingInvitations(@AuthenticationPrincipal User user) {
        return invitationService.getPendingInvitationsForUser(user);
    }

    @PostMapping("/{invId}/accept")
    public void accept(@PathVariable Long invId, @AuthenticationPrincipal User user) {
        invitationService.acceptInvitation(invId, user);
    }

    @PostMapping("/{invId}/decline")
    public void decline(@PathVariable Long invId, @AuthenticationPrincipal User user) {
        invitationService.declineInvitation(invId, user);
    }
}

