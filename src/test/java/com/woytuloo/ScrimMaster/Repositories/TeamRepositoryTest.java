package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");
    static {
        postgres.start();
    }
    @DynamicPropertySource
    static void configureProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
    }

    private User captain;
    private User player;

    @BeforeEach
    void setUp() {
        captain = new User("cpt", "123", "cpt@cpt.pl");
        player = new User("gracz", "pwd", "gracz@xd.pl");
        userRepository.saveAll(List.of(captain, player));

        Team team = new Team("TeamX", captain);
        team.getPlayers().add(player);
        team.setTeamRanking(1234);
        teamRepository.save(team);
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByTeamName_exists() {
        Optional<Team> team = teamRepository.findByTeamName("TeamX");
        assertThat(team).isPresent();
        assertThat(team.get().getCaptain().getUsername()).isEqualTo("cpt");
    }

    @Test
    void findByTeamName_notExists() {
        assertThat(teamRepository.findByTeamName("Nope")).isNotPresent();
    }

    @Test
    void findAllByCaptain_Username() {
        List<Team> teams = teamRepository.findAllByCaptain_Username("cpt");
        assertThat(teams).hasSize(1);
    }

    @Test
    void findAllByPlayers_Username() {
        List<Team> teams = teamRepository.findAllByPlayers_Username("gracz");
        assertThat(teams).hasSize(1);
        assertThat(teams.getFirst().getTeamName()).isEqualTo("TeamX");
    }
}
