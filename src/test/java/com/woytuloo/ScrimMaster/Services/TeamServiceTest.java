package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.TeamRequest;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock TeamRepository teamRepository;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock TeamInvitationService invitationService;

    TeamService service;

    @BeforeEach
    void setup() {
        service = new TeamService(teamRepository, userRepository, userService, invitationService);
    }

    @Test
    void addTeam() {
        Team t = new Team("t1", new User("a", "b", "c@a"));
        when(teamRepository.save(any(Team.class))).thenReturn(t);
        assertThat(service.addTeam(t)).isEqualTo(t);
    }

    @Test
    void getAllTeams() {
        when(teamRepository.findAll()).thenReturn(List.of(new Team("n", new User())));
        assertThat(service.getAllTeams()).hasSize(1);
    }

    @Test
    void getTeamById_found() {
        Team team = new Team("t2", new User());
        team.setTeamId(2L);


        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(team));


        Optional<Team> result = service.getTeamById(2L);

        assertThat(result).isPresent();
    }

    @Test
    void getTeamById_notFound() {
        when(teamRepository.findById(10L)).thenReturn(Optional.empty());
        assertThat(service.getTeamById(10L)).isEmpty();
    }

    @Test
    void getTeamByName_found() {
        Team t = new Team("tname", new User());
        when(teamRepository.findByTeamName("tname")).thenReturn(Optional.of(t));
        assertThat(service.getTeamByName("tname")).isPresent();
    }

    @Test
    void getTeamByName_notFound() {
        when(teamRepository.findByTeamName("t")).thenReturn(Optional.empty());
        assertThat(service.getTeamByName("t")).isEmpty();
    }

    @Test
    void deleteTeam() {
        service.deleteTeam(7L);
        verify(teamRepository).deleteById(any(Long.class));
    }

    @Test
    void updateTeam_exists() {
        Team t = new Team("xx", new User("cap", "pwd", "mail@a"));
        t.setTeamId(2L);
        when(teamRepository.findById(any(Long.class))).thenReturn(Optional.of(t));
        when(teamRepository.save(any(Team.class))).thenReturn(t);
        assertThat(service.updateTeam(t)).isNotNull();
    }

    @Test
    void updateTeam_notFound() {
        Team t = new Team("yy", new User());
        t.setTeamId(88L);
        when(teamRepository.findById(88L)).thenReturn(Optional.empty());
        assertThat(service.updateTeam(t)).isNull();
    }

    @Test
    void createTeam_invitationsCalled() {
        User cap = new User("u", "p", "mail");
        cap.setId(2L);

        TeamRequest req = new TeamRequest();
        req.setCaptainId(2L);
        req.setPlayerIds(new ArrayList<>());

        when(userRepository.findById(2L)).thenReturn(Optional.of(cap));
        when(teamRepository.save(any(Team.class))).thenReturn(new Team("n", cap));
        assertThat(service.createTeam(req)).isNotNull();
    }

    @Test
    void getCaptainsTeams() {
        User cap = new User("cc", "x", "x@x");
        cap.setId(4L);
        when(userRepository.findById(4L)).thenReturn(Optional.of(cap));
        when(teamRepository.findAllByCaptain_Username(anyString())).thenReturn(List.of(new Team("t", cap)));
        assertThat(service.getCaptainsTeams(4L)).hasSize(1);
    }

    @Test
    void updateTeamRanking() {
        Team t = new Team("t", new User());
        t.getPlayers().add(t.getCaptain());
        when(teamRepository.save(any(Team.class))).thenReturn(t);
        service.updateTeamRanking(t, 99);
        verify(teamRepository).save(any(Team.class));
        assertThat(t.getTeamRanking()).isEqualTo(99);
    }

    @Test
    void getPlayerTeams_found() {
        User u = new User("p", "p", "mail");
        when(userService.getCurrentUser()).thenReturn(Optional.of(u));
        when(teamRepository.findAllByPlayers_Username(anyString())).thenReturn(List.of(new Team("n", u)));
        assertThat(service.getPlayerTeams()).hasSize(1);
    }

    @Test
    void getPlayerTeams_notFound() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThat(service.getPlayerTeams()).isNull();
    }
}
