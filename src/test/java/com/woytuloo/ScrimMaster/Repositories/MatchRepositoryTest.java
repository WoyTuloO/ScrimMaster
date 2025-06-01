package com.woytuloo.ScrimMaster.Repositories;


import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MatchRepositoryTest {

    @Autowired
    private MatchRepository underTest;
    static List<Match> matches = new ArrayList<>();
    @Autowired
    private TeamRepository teamRepository;
    static List<Team> teams = new ArrayList<>();
    @Autowired
    private UserRepository userRepository;
    static List<User> users = new ArrayList<>();

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("moja_baza_testowa")
            .withUsername("user")
            .withPassword("password");

    static {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }


//    @BeforeEach
//    void init() {
//        User user1 = new User("UserOne", "password1", "userone@example.com");
//        user1.setKd(1.2);
//        user1.setAdr(2.3);
//        user1.setRanking(10);
//        user1.setRole("ROLE_USER");
//
//        User user2 = new User("UserTwo", "password2", "usertwo@example.com");
//        user2.setKd(2.3);
//        user2.setAdr(3.4);
//        user2.setRanking(20);
//        user2.setRole("ROLE_USER");
//
//        User user3 = new User("UserThree", "password3", "userthree@example.com");
//        user3.setKd(3.4);
//        user3.setAdr(4.5);
//        user3.setRanking(30);
//        user3.setRole("ROLE_USER");
//
//        User user4 = new User("UserFour", "password4", "userfour@example.com");
//        user4.setKd(4.5);
//        user4.setAdr(5.6);
//        user4.setRanking(40);
//        user4.setRole("ROLE_USER");
//
//        User user5 = new User("UserFive", "password5", "userfive@example.com");
//        user5.setKd(5.6);
//        user5.setAdr(6.7);
//        user5.setRanking(50);
//        user5.setRole("ROLE_USER");
//
//        User user6 = new User("UserSix", "password6", "usersix@example.com");
//        user6.setKd(6.7);
//        user6.setAdr(7.8);
//        user6.setRanking(60);
//        user6.setRole("ROLE_USER");
//
//        User user7 = new User("UserSeven", "password7", "userseven@example.com");
//        user7.setKd(7.8);
//        user7.setAdr(8.9);
//        user7.setRanking(70);
//        user7.setRole("ROLE_USER");
//
//        User user8 = new User("UserEight", "password8", "usereight@example.com");
//        user8.setKd(8.9);
//        user8.setAdr(9.0);
//        user8.setRanking(80);
//        user8.setRole("ROLE_ADMIN");
//
//        User user9 = new User("UserNine", "password9", "usernine@example.com");
//        user9.setKd(9.0);
//        user9.setAdr(10.1);
//        user9.setRanking(90);
//        user9.setRole("ROLE_ADMIN");
//
//        users = List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9);
//
//
//        Team team1 = new Team("Team1", user1);
//        team1.getPlayers().add(user2);
//        team1.getPlayers().add(user3);
//        team1.getPlayers().add(user4);
//        team1.getPlayers().add(user5);
//        team1.getPlayers().add(user6);
//        double teamranking = team1.getPlayers().stream()
//                .mapToDouble(User::getRanking)
//                .average()
//                .orElse(0.0);
//        team1.setTeamRanking((int) (teamranking / team1.getPlayers().size()));
//
//
//        Team team2 = new Team("Team2", user1);
//        team2.getPlayers().add(user2);
//        team2.getPlayers().add(user4);
//        team2.getPlayers().add(user6);
//        team2.getPlayers().add(user8);
//        teamranking = team2.getPlayers().stream()
//                .mapToDouble(User::getRanking)
//                .average()
//                .orElse(0.0);
//        team2.setTeamRanking((int) (teamranking / team2.getPlayers().size()));
//
//        Team team3 = new Team("Team3", user7);
//        team3.getPlayers().add(user6);
//        team3.getPlayers().add(user5);
//        team3.getPlayers().add(user4);
//        team3.getPlayers().add(user3);
//        teamranking = team3.getPlayers().stream()
//                .mapToDouble(User::getRanking)
//                .average()
//                .orElse(0.0);
//        team3.setTeamRanking((int) (teamranking / team3.getPlayers().size()));
//
//        Team team4 = new Team("Team4", user3);
//        team4.getPlayers().add(user4);
//        team4.getPlayers().add(user5);
//        team4.getPlayers().add(user6);
//        team4.getPlayers().add(user7);
//        teamranking = team4.getPlayers().stream()
//                .mapToDouble(User::getRanking)
//                .average()
//                .orElse(0.0);
//        team4.setTeamRanking((int) (teamranking / team4.getPlayers().size()));
//
//        teams = List.of(team1, team2, team3, team4);
//
//        Match match1 = new Match(team1, team2);
//        match1.setTeam1Score(16);
//        match1.setTeam2Score(14);
//
//        Match match2 = new Match(team3, team4);
//        match2.setTeam1Score(10);
//        match2.setTeam2Score(16);
//
//        Match match3 = new Match(team1, team3);
//        match3.setTeam1Score(12);
//        match3.setTeam2Score(16);
//
//        Match match4 = new Match(team2, team4);
//        match4.setTeam1Score(16);
//        match4.setTeam2Score(8);
//
//        matches = List.of(match1, match2, match3, match4);
//
//
//
//
//        userRepository.saveAll(users);
//        teamRepository.saveAll(teams);
//        underTest.saveAll(matches);
//        System.out.println("Matches added to ut: " + underTest.findAll());
//
//    }


    @Test
    void findById_notExists() {
        Optional<Match> match = underTest.findById(100);
        assertThat(match.isPresent()).isFalse();
    }

    @Test
    void deleteById_exists() {
        Match savedMatch = matches.getFirst();

        underTest.deleteById(savedMatch.getId());
        Optional<Match> match = underTest.findById(savedMatch.getId());
        assertThat(match.isPresent()).isFalse();
        assertThat(underTest.findAll().size()).isEqualTo(matches.size() - 1);
    }

    @Test
    void deleteById_notExists() {
        underTest.deleteById(100);
        assertThat(underTest.findAll().size()).isEqualTo(matches.size());
        Optional<Match> match = underTest.findById(100);
        assertThat(match.isPresent()).isFalse();
    }

    @Test
    void findByTeam_exists() {
        List<Match> matchList = underTest.findByTeamName(teams.get(0).getTeamName());
        assertThat(matchList.size()).isEqualTo(2);
    }

    @Test
    void findByTeam_notExists() {
        List<Match> matchList = underTest.findByTeamName("NotExistingTeam");
        assertThat(matchList.size()).isEqualTo(0);
    }

    @Test
    void findByUser_exists() {
        List<Match> matchList = underTest.findByUserName(users.get(0).getUsername());
        assertThat(matchList.size()).isEqualTo(3);
    }

    @Test
    void findByUser_notExists() {
        List<Match> matchList = underTest.findByUserName("NotExistingUser");
        assertThat(matchList.size()).isEqualTo(0);
    }

}