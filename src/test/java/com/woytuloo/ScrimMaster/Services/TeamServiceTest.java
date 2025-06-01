//package com.woytuloo.ScrimMaster.Services;
//
//
//import com.woytuloo.ScrimMaster.Models.Team;
//import com.woytuloo.ScrimMaster.Models.User;
//import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TeamServiceTest {
//
//    @Mock
//    private TeamRepository teamRepository;
//
//
//    private TeamService underTest;
//
//    @BeforeEach
//    void setUp() {
//        underTest = new TeamService(teamRepository);
//    }
//
//    @Test
//    void shallGetAllTeams() {
//        underTest.getAllTeams();
//        verify(teamRepository).findAll();
//    }
//
//    @Test
//    void shallGetTeamById() {
//        Long teamId = 1L;
//        when(teamRepository.findById(teamId)).thenReturn(java.util.Optional.of(new Team()));
//        underTest.getTeamById(teamId);
//        verify(teamRepository).findById(teamId);
//    }
//
//    @Test
//    void shallGetTeamByName() {
//        underTest.getTeamByName("Team A");
//        verify(teamRepository).findByTeamName("Team A");
//
//    }
//
//    @Test
//    void shallDeleteTeam() {
//        Long teamId = 1L;
//        underTest.deleteTeam(teamId);
//        verify(teamRepository).deleteById(teamId);
//    }
//
//    @Test
//    void shallUpdateTeam() {
//        User captain = new User("user", "password", "email@gmail.com");
//        Team team = new Team("Team A", captain);
//
//
//        underTest.updateTeam(team);
//        verify(teamRepository).findById(team.getTeamId());
//        then(teamRepository).should(never()).save(any(Team.class));
//    }
//
//
//    @Test
//    void shallUpdateTeam_teamIsPresent() {
//        User captain = new User("user", "password", "email@gmail.com");
//        Team team = new Team("Team A", captain);
//
//        given(teamRepository.findById(team.getTeamId())).willReturn(java.util.Optional.of(team));
//
//        underTest.updateTeam(team);
//        verify(teamRepository).findById(team.getTeamId());
//        verify(teamRepository).save(team);
//    }
//
//    @Test
//    void shallAddTeam() {
//        User captain = new User("user", "password", "email@gmail.com");
//        Team team = new Team("Team A", captain);
//
//        underTest.addTeam(team);
//        verify(teamRepository).save(team);
//    }
//
//}
