//package com.woytuloo.ScrimMaster.Repositories;
//
//
//import com.woytuloo.ScrimMaster.Models.Team;
//import com.woytuloo.ScrimMaster.Models.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@ActiveProfiles("test")
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class TeamRepositoryTest {
//
//    @Autowired
//    private TeamRepository underTest;
//    @Autowired
//    private UserRepository userRepository;
//
//    List<Team> teams = new ArrayList<>();
//
//
//
//    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
//            .withDatabaseName("moja_baza_testowa")
//            .withUsername("user")
//            .withPassword("password");
//
//    static {
//        postgresContainer.start();
//    }
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgresContainer::getUsername);
//        registry.add("spring.datasource.password", postgresContainer::getPassword);
//    }
//
//
//
//    @BeforeEach
//    void setUp() {
//
//        User user1 = new User("UserOne", "password1", "userone@example.com");
//        user1.setKd(1.2);
//        user1.setAdr(2.3);
//        user1.setRanking(10);
//        user1.setPersmissionLevel(0);
//
//        User user2 = new User("UserTwo", "password2", "usertwo@example.com");
//        user2.setKd(2.3);
//        user2.setAdr(3.4);
//        user2.setRanking(20);
//        user2.setPersmissionLevel(0);
//
//        User user3 = new User("UserThree", "password3", "userthree@example.com");
//        user3.setKd(3.4);
//        user3.setAdr(4.5);
//        user3.setRanking(30);
//        user3.setPersmissionLevel(0);
//
//        User user4 = new User("UserFour", "password4", "userfour@example.com");
//        user4.setKd(4.5);
//        user4.setAdr(5.6);
//        user4.setRanking(40);
//        user4.setPersmissionLevel(0);
//
//        User user5 = new User("UserFive", "password5", "userfive@example.com");
//        user5.setKd(5.6);
//        user5.setAdr(6.7);
//        user5.setRanking(50);
//        user5.setPersmissionLevel(0);
//
//        User user6 = new User("UserSix", "password6", "usersix@example.com");
//        user6.setKd(6.7);
//        user6.setAdr(7.8);
//        user6.setRanking(60);
//        user6.setPersmissionLevel(0);
//
//        User user7 = new User("UserSeven", "password7", "userseven@example.com");
//        user7.setKd(7.8);
//        user7.setAdr(8.9);
//        user7.setRanking(70);
//        user7.setPersmissionLevel(0);
//
//        User user8 = new User("UserEight", "password8", "usereight@example.com");
//        user8.setKd(8.9);
//        user8.setAdr(9.0);
//        user8.setRanking(80);
//        user8.setPersmissionLevel(0);
//
//        User user9 = new User("UserNine", "password9", "usernine@example.com");
//        user9.setKd(9.0);
//        user9.setAdr(10.1);
//        user9.setRanking(90);
//        user9.setPersmissionLevel(0);
//
//        userRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9));
//
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
//        team1.setTeamRanking((int) (teamranking/team1.getPlayers().size()));
//
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
//        team2.setTeamRanking((int) (teamranking/team2.getPlayers().size()));
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
//        team3.setTeamRanking((int) (teamranking/team3.getPlayers().size()));
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
//        team4.setTeamRanking((int) (teamranking/team4.getPlayers().size()));
//
//        teams.add(team1);
//        teams.add(team2);
//        teams.add(team3);
//        teams.add(team4);
//
//        underTest.save(team1);
//        underTest.save(team2);
//        underTest.save(team3);
//        underTest.save(team4);
//    }
//
//    @AfterEach
//    void tearDown() {
//        teams.clear();
//        underTest.deleteAll();
//    }
//
//    @Test
//    void testFindByTeamName_exists() {
//        Optional<Team> team1 = teams.stream().filter(team -> team.getTeamName().equals("Team1")).findFirst();
//        Optional<Team> team1ut = underTest.findByTeamName("Team1");
//        assertThat(team1ut).isEqualTo(team1);
//    }
//
//    @Test
//    void testFindByTeamName_nonExist() {
//        Optional<Team> team1 = teams.stream().filter(team -> team.getTeamName().equals("Team10")).findFirst();
//        Optional<Team> team1ut = underTest.findByTeamName("Team10");
//        assertThat(team1ut).isEqualTo(team1);
//    }
//
//    @Test
//    void testFindById_exists() {
//        Optional<Team> team1 = teams.stream().filter(team -> team.getTeamId() == 1).findFirst();
//        Optional<Team> team1ut = underTest.findById(1);
//        assertThat(team1ut).isEqualTo(team1);
//    }
//
//    @Test
//    void testFindById_nonExist() {
//        Optional<Team> team1 = teams.stream().filter(team -> team.getTeamId() == 10).findFirst();
//        Optional<Team> team1ut = underTest.findById(10);
//        assertThat(team1ut).isEqualTo(team1);
//    }
//
//    @Test
//    void testDeleteById_exist() {
//        underTest.deleteById(1);
//        assertThat(underTest.findById(1)).isEmpty();
//    }
//
//    @Test
//    void testDeleteById_nonExist() {
//        underTest.deleteById(10);
//        assertThat(underTest.count()).isEqualTo(4);
//    }
//
//    @Test
//    void testFindAllByCaptain_Username_exists() {
//        List<Team> team1 = underTest.findAllByCaptain_Username("UserOne");
//        team1.forEach(team -> assertThat(team.getCaptain().getUsername()).isEqualTo("UserOne"));
//    }
//
//    @Test
//    void testFindAllByCaptain_Username_nonExist() {
//        List<Team> team1 = underTest.findAllByCaptain_Username("UserTen");
//        assertThat(team1.isEmpty()).isTrue();
//    }
//
//    @Test
//    void testFindAllByPlayers_Username_exist() {
//        List<Team> team1 = underTest.findAllByPlayers_Username("UserOne");
//        team1.forEach(team -> assertThat(team.getPlayers().stream()
//                                    .anyMatch(user -> user.getUsername().equals("UserOne"))).isTrue());
//    }
//
//    @Test
//    void testFindAllByPlayers_Username_nonExist() {
//        List<Team> team1 = underTest.findAllByPlayers_Username("UserTen");
//        assertThat(team1.isEmpty()).isTrue();
//    }
//
//
//}
