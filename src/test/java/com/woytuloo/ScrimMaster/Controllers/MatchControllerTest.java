package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Config.SecurityConfig;
import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Security.JwtUtils;
import com.woytuloo.ScrimMaster.Services.MatchService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@Import({SecurityConfig.class, JwtUtils.class})
class MatchControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MatchService matchService;

    @MockBean
    UserRepository userRepo;

    @Autowired
    JwtUtils jwtUtils;

    private String jwt;

    @BeforeEach
    void setUp() {
        User user = new User("user", "{noop}password", "user@example.com");
        user.setRole("ROLE_USER");
        Mockito.when(userRepo.findByUsername("user")).thenReturn(Optional.of(user));
        jwt = jwtUtils.generateAccessToken("user", List.of("ROLE_USER"));
    }

    @Test
    void getMatchById_found() throws Exception {
        Match m = new Match();
        Mockito.when(matchService.getMatchById(9L)).thenReturn(Optional.of(m));
        mockMvc.perform(get("/api/match/9")
                        .cookie(new Cookie("accessToken", jwt)))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchById_notFound() throws Exception {
        Mockito.when(matchService.getMatchById(77L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/match/77")
                        .cookie(new Cookie("accessToken", jwt)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserMatches_ok() throws Exception {
        Mockito.when(matchService.getUserMatches()).thenReturn(List.of(new Match()));
        mockMvc.perform(get("/api/match/me")
                        .cookie(new Cookie("accessToken", jwt)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserMatches_unauthorized() throws Exception {
        Mockito.when(matchService.getUserMatches()).thenThrow(new RuntimeException("User not found"));
        mockMvc.perform(get("/api/match/me")
                        .cookie(new Cookie("accessToken", jwt)))
                .andExpect(status().isUnauthorized());
    }
}
