package com.woytuloo.ScrimMaster.Repositories;

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
class UserRepositoryTest {

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

    @BeforeEach
    void setUp() {
        User user1 = new User("ziutek", "haslo1", "ziutek@wp.pl");
        user1.setKd(1.2);
        user1.setAdr(30.3);
        user1.setRanking(2000);
        user1.setRole("ROLE_USER");

        User user2 = new User("alina", "haslo2", "alina@onet.pl");
        user2.setKd(0.9);
        user2.setAdr(18.7);
        user2.setRanking(1000);
        user2.setRole("ROLE_ADMIN");

        userRepository.saveAll(List.of(user1, user2));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_existing() {
        Optional<User> u = userRepository.findByUsername("ziutek");
        assertThat(u).isPresent();
        assertThat(u.get().getEmail()).isEqualTo("ziutek@wp.pl");
    }

    @Test
    void findByUsername_notExisting() {
        assertThat(userRepository.findByUsername("nieistniejacy")).isNotPresent();
    }

    @Test
    void findByEmail_existing() {
        assertThat(userRepository.findByEmail("alina@onet.pl")).isPresent();
    }

    @Test
    void existsByUsername() {
        assertThat(userRepository.existsByUsername("alina")).isTrue();
        assertThat(userRepository.existsByUsername("wrong")).isFalse();
    }

    @Test
    void deleteByUsername() {
        userRepository.deleteByUsername("alina");
        assertThat(userRepository.findByUsername("alina")).isNotPresent();
    }

    @Test
    void findByUsernameContainingIgnoreCase() {
        List<User> found = userRepository.findByUsernameContainingIgnoreCase("ZIU");
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getUsername()).isEqualTo("ziutek");
    }

    @Test
    void findByEmailContainingIgnoreCase() {
        List<User> found = userRepository.findByEmailContainingIgnoreCase("ONET");
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getUsername()).isEqualTo("alina");
    }
}
