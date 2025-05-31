package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.TeamInvitationDTO;
import com.woytuloo.ScrimMaster.Models.InvitationStatus;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.TeamInvitation;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.TeamInvitationRepository;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamInvitationService {
    private final TeamInvitationRepository invitationRepo;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;

    public List<TeamInvitationDTO> getPendingInvitationsForUser(User user) {
        return invitationRepo.findByInvitedUserAndStatus(user, InvitationStatus.PENDING)
                .stream()
                .map(inv -> new TeamInvitationDTO(inv.getId(), inv.getTeam().getTeamName(), inv.getInvitedBy().getUsername()))
                .toList();
    }

    @Transactional
    public void acceptInvitation(Long invitationId, User user) {
        TeamInvitation inv = invitationRepo.findById(invitationId).orElseThrow();
        if (!inv.getInvitedUser().equals(user) || inv.getStatus() != InvitationStatus.PENDING)
            throw new RuntimeException("Nieautoryzowane lub już obsłużone!");
        inv.setStatus(InvitationStatus.ACCEPTED);
        invitationRepo.save(inv);
        Team team = inv.getTeam();
        team.getPlayers().add(user);
        teamRepo.save(team);
    }

    @Transactional
    public void declineInvitation(Long invitationId, User user) {
        TeamInvitation inv = invitationRepo.findById(invitationId).orElseThrow();
        if (!inv.getInvitedUser().equals(user) || inv.getStatus() != InvitationStatus.PENDING)
            throw new RuntimeException("Nieautoryzowane lub już obsłużone!");
        inv.setStatus(InvitationStatus.DECLINED);
        invitationRepo.save(inv);
    }

    @Transactional
    public void inviteUsersToTeam(List<Long> userIds, Team team, User captain) {
        for (Long playerId : userIds) {
            if (!playerId.equals(captain.getId())) {
                User invited = userRepo.findById(playerId).orElseThrow();
                TeamInvitation inv = new TeamInvitation();
                inv.setTeam(team);
                inv.setInvitedUser(invited);
                inv.setInvitedBy(captain);
                inv.setStatus(InvitationStatus.PENDING);
                inv.setSentAt(java.time.LocalDateTime.now());
                invitationRepo.save(inv);
            }
        }
    }
}

