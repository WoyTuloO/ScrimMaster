package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.PublicMessage;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PublicMessageRepositoryTest {

    @Autowired
    private PublicMessageRepository publicMessageRepository;

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

    @BeforeEach
    void setUp() {
        publicMessageRepository.save(new PublicMessage("a", "hejka", Instant.now()));
        publicMessageRepository.save(new PublicMessage("b", "test", Instant.now()));
    }

    @AfterEach
    void tearDown() {
        publicMessageRepository.deleteAll();
    }

    @Test
    void findLatest() {
        List<PublicMessage> latest = publicMessageRepository.findLatest(2);
        assertThat(latest).hasSize(2);
    }
}
