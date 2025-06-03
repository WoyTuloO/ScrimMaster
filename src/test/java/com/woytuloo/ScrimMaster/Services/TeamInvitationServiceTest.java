package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.TeamInvitationDTO;
import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.TeamInvitationRepository;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class TeamInvitationServiceTest {

    @Mock private TeamInvitationRepository invitationRepo;
    @Mock private UserRepository userRepo;
    @Mock private TeamRepository teamRepo;
    @Mock private UserService userService;
    @InjectMocks private TeamInvitationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPendingInvitationsForUser_noUser_throws() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getPendingInvitationsForUser());
    }

    @Test
    void getPendingInvitationsForUser_returnsInvitations() {
        User user = new User();
        user.setId(1L);
        user.setUsername("player1");

        Team team = new Team();
        team.setTeamName("Testers");

        User invitedBy = new User();
        invitedBy.setUsername("captain");

        TeamInvitation invitation = new TeamInvitation();
        invitation.setId(5L);
        invitation.setInvitedUser(user);
        invitation.setTeam(team);
        invitation.setInvitedBy(invitedBy);
        invitation.setStatus(InvitationStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(invitationRepo.findByInvitedUserAndStatus(user, InvitationStatus.PENDING))
                .thenReturn(List.of(invitation));

        List<TeamInvitationDTO> result = service.getPendingInvitationsForUser();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeamName()).isEqualTo("Testers");
        assertThat(result.get(0).getId()).isEqualTo(5L);
    }

    @Test
    void acceptInvitation_noUser_throws() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.acceptInvitation(1L));
    }

    @Test
    void acceptInvitation_wrongUserOrStatus_throws() {
        User user = new User();
        user.setId(1L);

        User another = new User();
        another.setId(2L);

        Team team = new Team();
        team.setTeamName("Alpha");

        TeamInvitation invitation = new TeamInvitation();
        invitation.setId(2L);
        invitation.setInvitedUser(another);
        invitation.setTeam(team);
        invitation.setStatus(InvitationStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(invitationRepo.findById(2L)).thenReturn(Optional.of(invitation));

        assertThrows(RuntimeException.class, () -> service.acceptInvitation(2L));

        invitation.setInvitedUser(user);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        assertThrows(RuntimeException.class, () -> service.acceptInvitation(2L));
    }

    @Test
    void acceptInvitation_works() {
        User user = new User();
        user.setId(3L);

        Team team = new Team();
        team.setTeamName("Beta");
        team.setPlayers(new ArrayList<>());

        TeamInvitation invitation = new TeamInvitation();
        invitation.setId(3L);
        invitation.setInvitedUser(user);
        invitation.setTeam(team);
        invitation.setStatus(InvitationStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(invitationRepo.findById(3L)).thenReturn(Optional.of(invitation));

        service.acceptInvitation(3L);

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        assertThat(team.getPlayers()).contains(user);
        verify(invitationRepo).save(invitation);
        verify(teamRepo).save(team);
    }

    @Test
    void declineInvitation_noUser_throws() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.declineInvitation(1L));
    }

    @Test
    void declineInvitation_wrongUserOrStatus_throws() {
        User user = new User();
        user.setId(4L);

        User another = new User();
        another.setId(5L);

        TeamInvitation invitation = new TeamInvitation();
        invitation.setId(4L);
        invitation.setInvitedUser(another);
        invitation.setStatus(InvitationStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(invitationRepo.findById(4L)).thenReturn(Optional.of(invitation));

        assertThrows(RuntimeException.class, () -> service.declineInvitation(4L));

        invitation.setInvitedUser(user);
        invitation.setStatus(InvitationStatus.DECLINED);
        assertThrows(RuntimeException.class, () -> service.declineInvitation(4L));
    }

    @Test
    void declineInvitation_works() {
        User user = new User();
        user.setId(6L);

        TeamInvitation invitation = new TeamInvitation();
        invitation.setId(6L);
        invitation.setInvitedUser(user);
        invitation.setStatus(InvitationStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(invitationRepo.findById(6L)).thenReturn(Optional.of(invitation));

        service.declineInvitation(6L);

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.DECLINED);
        verify(invitationRepo).save(invitation);
    }

    @Test
    void inviteUsersToTeam_invitesAllExceptCaptain() {
        User captain = new User();
        captain.setId(10L);

        User invited1 = new User();
        invited1.setId(11L);

        User invited2 = new User();
        invited2.setId(12L);

        Team team = new Team();
        team.setTeamName("Gamma");

        when(userRepo.findById(11L)).thenReturn(Optional.of(invited1));
        when(userRepo.findById(12L)).thenReturn(Optional.of(invited2));

        service.inviteUsersToTeam(List.of(10L, 11L, 12L), team, captain);

        ArgumentCaptor<TeamInvitation> captor = ArgumentCaptor.forClass(TeamInvitation.class);
        verify(invitationRepo, times(2)).save(captor.capture());

        List<TeamInvitation> saved = captor.getAllValues();

        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getInvitedUser()).isIn(invited1, invited2);
        assertThat(saved.get(1).getInvitedUser()).isIn(invited1, invited2);
        assertThat(saved.get(0).getInvitedBy()).isEqualTo(captain);
        assertThat(saved.get(0).getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(saved.get(1).getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(saved.get(0).getTeam()).isEqualTo(team);
        assertThat(saved.get(1).getTeam()).isEqualTo(team);
    }
}
