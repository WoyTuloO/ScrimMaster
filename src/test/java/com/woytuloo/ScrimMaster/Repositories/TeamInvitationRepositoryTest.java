package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.*;
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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TeamInvitationRepositoryTest {

    @Autowired
    private TeamInvitationRepository invitationRepository;
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
    static void configure(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
    }

    private Team team;
    private User captain, invited;

    @BeforeEach
    void setUp() {
        captain = new User("kapitan", "haslo", "k@t.pl");
        invited = new User("zaproszony", "haslo", "z@t.pl");
        userRepository.saveAll(List.of(captain, invited));
        team = new Team("TeamI", captain);
        teamRepository.save(team);

        TeamInvitation inv = new TeamInvitation();
        inv.setTeam(team);
        inv.setInvitedBy(captain);
        inv.setInvitedUser(invited);
        inv.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(inv);
    }

    @AfterEach
    void tearDown() {
        invitationRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByInvitedUserAndStatus() {
        List<TeamInvitation> invs = invitationRepository.findByInvitedUserAndStatus(invited, InvitationStatus.PENDING);
        assertThat(invs).hasSize(1);
        assertThat(invs.get(0).getInvitedBy().getUsername()).isEqualTo("kapitan");
    }
}
