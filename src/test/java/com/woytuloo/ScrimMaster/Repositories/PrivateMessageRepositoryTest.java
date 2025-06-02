package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Limit;
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
class PrivateMessageRepositoryTest {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

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

    private ChatRoom room;

    @BeforeEach
    void setUp() {
        room = new ChatRoom("room1", "a", "b");
        chatRoomRepository.save(room);
        privateMessageRepository.save(new PrivateMessage(room, "a", "hello", Instant.now()));
        privateMessageRepository.save(new PrivateMessage(room, "b", "hi", Instant.now()));
    }

    @AfterEach
    void tearDown() {
        privateMessageRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }

    @Test
    void findLatestByRoom() {
        List<PrivateMessage> msgs = privateMessageRepository.findLatestByRoom("room1", Limit.of(2));
        assertThat(msgs).hasSize(2);
        assertThat(msgs.getFirst().getChatRoom().getId()).isEqualTo("room1");
    }
}
