package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;
    List<User> users = new ArrayList<>();

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



    @BeforeEach
    void setUp() {
        User user1 = new User("UserOne", "password1", "userone@example.com");
        user1.setKd(1.2);
        user1.setAdr(2.3);
        user1.setRanking(10);
        user1.setPersmissionLevel(1);

        User user2 = new User("UserTwo", "password2", "usertwo@example.com");
        user2.setKd(2.3);
        user2.setAdr(3.4);
        user2.setRanking(20);
        user2.setPersmissionLevel(2);

        User user3 = new User("UserThree", "password3", "userthree@example.com");
        user3.setKd(3.4);
        user3.setAdr(4.5);
        user3.setRanking(30);
        user3.setPersmissionLevel(3);

        User user4 = new User("UserFour", "password4", "userfour@example.com");
        user4.setKd(4.5);
        user4.setAdr(5.6);
        user4.setRanking(40);
        user4.setPersmissionLevel(4);

        User user5 = new User("UserFive", "password5", "userfive@example.com");
        user5.setKd(5.6);
        user5.setAdr(6.7);
        user5.setRanking(50);
        user5.setPersmissionLevel(5);

        underTest.save(user1);
        underTest.save(user2);
        underTest.save(user3);
        underTest.save(user4);
        underTest.save(user5);

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }


    @Test
    void sampleTest() {
        String jdbcUrl = postgresContainer.getJdbcUrl();
        assertThat(jdbcUrl).contains("postgres");
    }

    @Test
    void deleteByUsername() {
        underTest.deleteByUsername("UserOne");
        assertThat(underTest.count()).isEqualTo(4);
        assertThat(underTest.findByUsername("UserOne")).isEmpty();
    }

    @Test
    void deleteByUsername_nonExistant() {
        underTest.deleteByUsername("UserSix");
        assertThat(underTest.count()).isEqualTo(5);
    }

    @Test
    void findByUsername() {
        assertThat(underTest.findByUsername("UserOne").get().getUsername()).isEqualTo("UserOne");
    }

    @Test
    void findByUsername_fail() {
        assertThat(underTest.findByUsername("UserSix").isPresent()).isFalse();
    }


    @Test
    void findByEmail() {
        assertThat(underTest.findByEmail("userone@example.com").get().getEmail()).isEqualTo("userone@example.com");
    }

    @Test
    void findByEmail_fail() {
        assertThat(underTest.findByEmail("usersix@example.com").isPresent()).isFalse();
    }

    @Test
    void findByUsernameContainingIgnoreCase() {
        assertThat(underTest.count()).isEqualTo(underTest.findByUsernameContainingIgnoreCase("User").size());
        assertThat(underTest.findByUsernameContainingIgnoreCase("User")).isEqualTo(users);
    }

    @Test
    void findByUsernameContainingIgnoreCase_spec() {
        assertThat(underTest.findByUsernameContainingIgnoreCase("two")).isEqualTo(users.stream()
                .filter(u->u.getUsername().toLowerCase().contains("two")).collect(Collectors.toList()));
    }

    @Test
    void findByEmailContainingIgnoreCase() {
        assertThat(underTest.count()).isEqualTo(underTest.findByEmailContainingIgnoreCase("user").size());
        assertThat(underTest.findByEmailContainingIgnoreCase("user")).isEqualTo(users);
    }

    @Test
    void findByEmailContainingIgnoreCase_spec() {
        assertThat(underTest.findByEmailContainingIgnoreCase("two")).isEqualTo(users.stream()
                .filter(u->u.getEmail().toLowerCase().contains("two")).collect(Collectors.toList()));
    }
}