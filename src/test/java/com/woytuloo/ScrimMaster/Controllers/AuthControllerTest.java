package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.Models.RefreshToken;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Security.JwtUtils;
import com.woytuloo.ScrimMaster.Services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthenticationManager authManager;
    @MockBean JwtUtils jwtUtils;
    @MockBean RefreshTokenService refreshSvc;
    @MockBean UserRepository userRepo;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void login_success() throws Exception {
        User creds = new User("demo", "secret", "demo@mail.com");
        creds.setRole("ROLE_USER");

        User dbUser = new User("demo", "secret", "demo@mail.com");
        dbUser.setRole("ROLE_USER");
        dbUser.setId(1L);

        RefreshToken refresh = new RefreshToken();
        refresh.setToken("token123");
        refresh.setUser(dbUser);
        refresh.setExpiryDate(Instant.now().plusSeconds(10000));

        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(authManager.authenticate(any())).thenReturn(mockAuth);
        Mockito.when(userRepo.findByUsername("demo")).thenReturn(Optional.of(dbUser));
        Mockito.when(jwtUtils.generateAccessToken(anyString(), anyList())).thenReturn("access-token");
        Mockito.when(refreshSvc.createRefreshToken(any())).thenReturn(refresh);
        Mockito.when(jwtUtils.getAccessExpirationMs()).thenReturn(3600000L);
        Mockito.when(jwtUtils.getRefreshExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isOk());
    }

    @Test
    void login_userNotFound() throws Exception {
        User creds = new User("nouser", "secret", "x@x");
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(authManager.authenticate(any())).thenReturn(mockAuth);
        Mockito.when(userRepo.findByUsername("nouser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_success() throws Exception {
        User dbUser = new User("demo", "secret", "demo@mail.com");
        dbUser.setRole("ROLE_USER");
        dbUser.setId(1L);

        RefreshToken refresh = new RefreshToken();
        refresh.setToken("token123");
        refresh.setUser(dbUser);
        refresh.setExpiryDate(Instant.now().plusSeconds(10000));

        Mockito.when(refreshSvc.findByToken("token123")).thenReturn(Optional.of(refresh));
        Mockito.when(refreshSvc.verifyExpiration(refresh)).thenReturn(refresh);
        Mockito.when(jwtUtils.generateAccessToken(anyString(), anyList())).thenReturn("access-token");
        Mockito.when(jwtUtils.getAccessExpirationMs()).thenReturn(3600000L);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "token123")))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken_notFound() throws Exception {
        Mockito.when(refreshSvc.findByToken("notfound")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "notfound")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_withRefreshToken() throws Exception {
        User dbUser = new User("demo", "secret", "demo@mail.com");
        dbUser.setRole("ROLE_USER");
        dbUser.setId(1L);
        RefreshToken refresh = new RefreshToken();
        refresh.setUser(dbUser);

        Mockito.when(refreshSvc.findByToken("token123")).thenReturn(Optional.of(refresh));
        doNothing().when(refreshSvc).deleteByUser(any());
        Mockito.when(jwtUtils.getAccessExpirationMs()).thenReturn(3600000L);
        Mockito.when(jwtUtils.getRefreshExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "token123")))
                .andExpect(status().isOk());
    }

    @Test
    void logout_withAuthNoToken() throws Exception {
        User dbUser = new User("demo", "secret", "demo@mail.com");
        dbUser.setRole("ROLE_USER");
        dbUser.setId(1L);

        Mockito.when(userRepo.findByUsername("demo")).thenReturn(Optional.of(dbUser));
        doNothing().when(refreshSvc).deleteByUser(any());
        Mockito.when(jwtUtils.getAccessExpirationMs()).thenReturn(3600000L);
        Mockito.when(jwtUtils.getRefreshExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/api/auth/logout")
                        .principal(() -> "demo"))
                .andExpect(status().isOk());
    }
}
