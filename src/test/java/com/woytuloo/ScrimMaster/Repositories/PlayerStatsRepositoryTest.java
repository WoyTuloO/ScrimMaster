package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.PlayerStats;
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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlayerStatsRepositoryTest {

    @Autowired
    private PlayerStatsRepository playerStatsRepository;
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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("kappa", "passwd", "kappa@kappa.com");
        userRepository.save(user);
        PlayerStats stats = new PlayerStats();
        stats.setPlayer(user);
        stats.setTeamSide(1);
        stats.setKd(1.1);
        stats.setAdr(22.2);
        playerStatsRepository.save(stats);
    }

    @AfterEach
    void tearDown() {
        playerStatsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveAndFind() {
        assertThat(playerStatsRepository.findAll()).hasSize(1);
        PlayerStats stats = playerStatsRepository.findAll().get(0);
        assertThat(stats.getPlayer().getUsername()).isEqualTo("kappa");
        assertThat(stats.getKd()).isEqualTo(1.1);
    }
}
