package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
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
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository repo;

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
        ChatRoom r1 = new ChatRoom("room1", "ziutek", "alina");
        ChatRoom r2 = new ChatRoom("room2", "alina", "krzychu");
        repo.saveAll(List.of(r1, r2));
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
    }

    @Test
    void findChatRoomById_existing() {
        ChatRoom r = repo.findChatRoomById("room1");
        assertThat(r).isNotNull();
        assertThat(r.getUserA()).isEqualTo("ziutek");
    }

    @Test
    void findChatRoomById_notExisting() {
        assertThat(repo.findChatRoomById("wrong")).isNull();
    }

    @Test
    void findChatRoomsByUserAOrUserB() {
        List<ChatRoom> rooms = repo.findChatRoomsByUserAOrUserB("alina", "alina");
        assertThat(rooms).hasSize(2);
    }
}
