package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.PlayerStats;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.MatchRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @Mock MatchRepository matchRepository;
    @Mock UserService userService;
    @Mock TeamService teamService;
    @Mock UserRepository userRepository;
    Match match;

    @InjectMocks MatchService matchService;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this);

        User user1 = new User("UserOne", "password1", "userone@example.com");
        user1.setKd(1.2);
        user1.setAdr(2.3);
        user1.setRanking(10);
        user1.setRole("ROLE_USER");

        User user2 = new User("UserTwo", "password2", "usertwo@example.com");
        user2.setKd(2.3);
        user2.setAdr(3.4);
        user2.setRanking(20);
        user2.setRole("ROLE_USER");

        User user3 = new User("UserThree", "password3", "userthree@example.com");
        user3.setKd(3.4);
        user3.setAdr(4.5);
        user3.setRanking(30);
        user3.setRole("ROLE_USER");

        User user4 = new User("UserFour", "password4", "userfour@example.com");
        user4.setKd(4.5);
        user4.setAdr(5.6);
        user4.setRanking(40);
        user4.setRole("ROLE_USER");

        PlayerStats ps1 = new PlayerStats();
        ps1.setPlayer(user1);
        ps1.setTeamSide(1);
        ps1.setKd(1.10);
        ps1.setAdr(2.10);

        PlayerStats ps2 = new PlayerStats();
        ps2.setPlayer(user2);
        ps2.setTeamSide(1);
        ps2.setKd(0.98);
        ps2.setAdr(2.23);

        PlayerStats ps3 = new PlayerStats();
        ps3.setPlayer(user3);
        ps3.setTeamSide(2);
        ps3.setKd(1.45);
        ps3.setAdr(3.00);

        PlayerStats ps4 = new PlayerStats();
        ps4.setPlayer(user4);
        ps4.setTeamSide(2);
        ps4.setKd(1.32);
        ps4.setAdr(2.87);

        List<PlayerStats> team1Stats = List.of(ps1, ps2);
        List<PlayerStats> team2Stats = List.of(ps3, ps4);

        match = new Match(
                "Team1",
                "Team2",
                16,
                12,
                team1Stats,
                team2Stats
        );

        for (PlayerStats ps : team1Stats) {
            ps.setMatch(match);
        }
        for (PlayerStats ps : team2Stats) {
            ps.setMatch(match);
        }

        userRepository.saveAll(List.of(user1, user2, user3, user4));
        matchRepository.save(match);


    }

    @Test void getMatchById_found() {
        Match m = new Match();
        when(matchRepository.findById(1L)).thenReturn(Optional.of(m));
        assertThat(matchService.getMatchById(1L)).isPresent();
    }
    @Test void getMatchById_notFound() {
        when(matchRepository.findById(2L)).thenReturn(Optional.empty());
        assertThat(matchService.getMatchById(2L)).isEmpty();
    }

    @Test void addMatch_savesToRepo() {
        when(matchRepository.save(any())).thenReturn(match);
        assertThat(matchService.addMatch(match)).isEqualTo(match);
    }

    @Test void getUserMatches_currentUserFound() {
        User u = new User("x", "y", "z@a");
        when(userService.getCurrentUser()).thenReturn(Optional.of(u));
        when(matchRepository.findByUserName(anyString())).thenReturn(List.of(new Match()));
        assertThat(matchService.getUserMatches()).hasSize(1);
    }

    @Test void getUserMatches_userNotFound() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> matchService.getUserMatches());
    }
}
