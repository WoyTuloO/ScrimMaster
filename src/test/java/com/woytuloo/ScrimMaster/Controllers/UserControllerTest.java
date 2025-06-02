package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.DTO.ChangeNicknameRequest;
import com.woytuloo.ScrimMaster.DTO.ChangePasswordRequest;
import com.woytuloo.ScrimMaster.DTO.TeamRequest;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Services.UserService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean UserService userService;
    @Autowired ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;

    @Test void getUsers_byId_found() throws Exception {
        User u = new User("xx","pass","x@x");
        u.setId(10L);
        doReturn(Optional.of(u)).when(userService).getUserById(10L);
        mockMvc.perform(get("/api/user?id=10"))
                .andExpect(status().isOk());
    }
    @Test void getUsers_byId_notFound() throws Exception {
        doReturn(Optional.empty()).when(userService).getUserById(77L);
        mockMvc.perform(get("/api/user?id=77"))
                .andExpect(status().isNotFound());
    }
    @Test void getUsers_byUsername() throws Exception {
        doReturn(List.of(new User())).when(userService).getUsersByName("abc");
        mockMvc.perform(get("/api/user?username=abc"))
                .andExpect(status().isOk());
    }
    @Test void getUsers_byEmail() throws Exception {
        doReturn(List.of(new User())).when(userService).getUsersByEmail("e@ma.il");
        mockMvc.perform(get("/api/user?email=e@ma.il"))
                .andExpect(status().isOk());
    }
    @Test void getUsers_allUsers() throws Exception {
        doReturn(List.of(new User())).when(userService).getAllUsers();
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk());
    }
    @Test void getUsers_noneFound() throws Exception {
        doReturn(List.of()).when(userService).getAllUsers();
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isNotFound());
    }
    @Test void getCurrentUser_found() throws Exception {
        User u = new User("cur","pwd","e@ma.il");
        doReturn(Optional.of(u)).when(userService).getCurrentUser();
        mockMvc.perform(get("/api/user/currentUser"))
                .andExpect(status().isOk());
    }
    @Test void getCurrentUser_notFound() throws Exception {
        doReturn(Optional.empty()).when(userService).getCurrentUser();
        mockMvc.perform(get("/api/user/currentUser"))
                .andExpect(status().isUnauthorized());
    }
    @Test void addUser() throws Exception {
        User u = new User("nick","pwd","a@b.com");
        doReturn(u).when(userService).addUser(any());
        mockMvc.perform(post("/api/user").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteUser_found() throws Exception {
        User u = new User("nick","pwd","a@b.com");
        u.setId(5L);
        doReturn(1).when(userService).deleteUserByUsername(u.getUsername());
        mockMvc.perform(delete("/api/user").with(user("nick").password("pwd").roles("USER")).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        User u = new User("nick","pwd","a@b.com");
        u.setId(15L);
        doReturn(0).when(userService).deleteUserByUsername(u.getUsername());
        mockMvc.perform(delete("/api/user").with(user("nick").password("pwd").roles("USER")).with(csrf()))
                .andExpect(status().isNotFound());
    }


    @Test void changeUsername() throws Exception {
        ChangeNicknameRequest req = new ChangeNicknameRequest();
        req.setNewUsername("nowszyNick");
        doReturn(ResponseEntity.ok().build()).when(userService).changeNickname(anyString());
        mockMvc.perform(put("/api/user/nickname").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
    @Test void changePassword() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("old"); req.setNewPassword("new");
        doReturn(ResponseEntity.ok().build()).when(userService).changePassword(anyString(), anyString());
        mockMvc.perform(put("/api/user/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
