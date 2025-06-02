package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Security.JwtUtils;
import com.woytuloo.ScrimMaster.Services.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(JwtUtils.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private UserRepository userRepo;

    private String jwt;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        if (userRepo.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "{noop}password", "admin@example.com");
            admin.setRole("ROLE_ADMIN");
            userRepo.save(admin);
        }
        jwt = jwtUtils.generateAccessToken("admin", List.of("ROLE_ADMIN"));
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .cookie(new Cookie("accessToken", jwt)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_found() throws Exception {
        User u = userRepo.save(new User("testu", "{noop}haslo", "testu@x.pl"));
        mockMvc.perform(delete("/api/admin/users/" + u.getId())
                        .cookie(new Cookie("accessToken", jwt))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/api/admin/users/9999")
                        .cookie(new Cookie("accessToken", jwt))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}

