package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void getAllUsers() throws Exception {
        var u1 = new User("Alice", "pw", "a@x.pl");
        var u2 = new User("Bob",   "pw", "b@x.pl");

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("Alice"))
                .andExpect(jsonPath("$[1].username").value("Bob"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        Mockito.when(userService.getUserById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user").param("id", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByUsername() throws Exception {
        var bob = new User("Bob", "pw", "b@x.pl");
        Mockito.when(userService.getUsersByName("Bob")).thenReturn(List.of(bob));

        mockMvc.perform(get("/api/user").param("username", "Bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("b@x.pl"));
    }


    @Nested
    class CurrentUser {

        @Test
        void whenLoggedIn_returnsUserDto() throws Exception {
            var alice = new User("Alice", "pw", "a@x.pl");
            Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(alice));

            mockMvc.perform(get("/api/user/currentUser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("Alice"))
                    .andExpect(jsonPath("$.email").value("a@x.pl"));
        }

        @Test
        void whenNotLoggedIn_returns401() throws Exception {
            Mockito.when(userService.getCurrentUser()).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/user/currentUser"))
                    .andExpect(status().isUnauthorized());
        }
    }


    @Test
    void addUser_returns201_andBody() throws Exception {
        var toCreate = new User("NewGuy", "pw", "n@x.pl");
        var saved    = new User("NewGuy", "pwHASH", "n@x.pl");
        saved.setId(10L);

        Mockito.when(userService.addUser(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("n@x.pl"));
    }


    @Test
    void updateUser_ok() throws Exception {
        var incoming = new User("Alice", "pw", "alice@x.pl");  incoming.setId(1L);
        var updated  = new User("Alice", "pwHASH", "alice@x.pl"); updated.setId(1L);

        Mockito.when(userService.updateUser(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateUser_notFound() throws Exception {
        Mockito.when(userService.updateUser(any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new User())))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteUser_returns200() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 99))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUserById(eq(99L));
    }
}
