package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shallGetAllUsers() {
        underTest.getAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    void shallGetUsersByName() {
        underTest.getUsersByName("John");
        verify(userRepository).findByUsernameContainingIgnoreCase("John");
    }

    @Test
    void shallGetUsersByEmail() {
        underTest.getUsersByEmail("john@example.com");
        verify(userRepository).findByEmailContainingIgnoreCase("john@example.com");
    }

    @Test
    void shallDeleteUserByUsername() {
        underTest.deleteUserByUsername("John");
        verify(userRepository).deleteByUsername("John");
    }

    @Test
    void shallDeleteUserById() {
        underTest.deleteUserById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shallGetUserById() {
        underTest.getUserById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void shallAddUser() {
        User user = new User(
                "Woytuloo",
                "password",
                "woytuloomail@gmail.com"
        );
        underTest.addUser(user);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);

    }

    @Test
    void shallThrowWhenAddingUserWithTakenUsername() {
        User user = new User(
                "Woytuloo",
                "password",
                "woytuloomail@gmail.com"
        );

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));

        assertThatThrownBy(()->underTest.addUser(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Użytkownik o takim nicku już istnieje");


        verify(userRepository, never()).save(any());


    }

    @Test
    void shallThrowWhenAddingUserWithTakenEmail() {
        User user = new User(
                "Woytuloo",
                "password",
                "woytuloomail@gmail.com"
        );

        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        assertThatThrownBy(()->underTest.addUser(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Użytkownik o takim adresie email już istnieje");


        verify(userRepository, never()).save(any());


    }

    @Test
    void shallUpdateUser() {
        User user = new User(
                "Woytuloo",
                "password",
                "woytuloomail@gmail.com"
        );
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        given(userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));

        underTest.updateUser(user);

        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);

    }

    @Test
    void shallNotUpdateNotExistingUser() {
        User user = new User(
                "Woytuloo",
                "password",
                "woytuloomail@gmail.com"
        );

        given(userRepository.findById(user.getId()))
                .willReturn(Optional.empty());
        underTest.updateUser(user);
        verify(userRepository, never()).save(any());

    }

    @Test
    void shallGetCurrentUser() {
        String username = "Woytuloo";
        User user = new User(username, "password", "woytuloomail@gmail.com");

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);

        given(auth.isAuthenticated()).willReturn(true);
        given(auth.getPrincipal()).willReturn(username);


        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        given(auth.getName()).willReturn(username);

        underTest.getCurrentUser();

        ArgumentCaptor<String> userArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(username);
    }

    @Test
    void shallNotGetCurrentUser_NotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        given(auth.isAuthenticated()).willReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        underTest.getCurrentUser();
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void shallNotGetCurrentUser_AnonymousUserPrincipal() {
        Authentication auth = mock(Authentication.class);

        given(auth.isAuthenticated()).willReturn(true);
        given(auth.getPrincipal()).willReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        underTest.getCurrentUser();
        verify(userRepository, never()).findByUsername(any());

    }

    @Test
    void shallNotGetCurrentUser_AuthIsNull() {
        Authentication auth = null;

        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        underTest.getCurrentUser();
        verify(userRepository, never()).findByUsername(any());

    }

}