package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.RefreshToken;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
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
        user = new User("rtuser", "secret", "rt@ex.com");
        userRepository.save(user);
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setToken("sometoken");
        refreshTokenRepository.save(token);
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByToken_existing() {
        Optional<RefreshToken> t = refreshTokenRepository.findByToken("sometoken");
        assertThat(t).isPresent();
        assertThat(t.get().getUser().getUsername()).isEqualTo("rtuser");
    }

    @Test
    void deleteByUser() {
        refreshTokenRepository.deleteByUser(user);
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }
}
