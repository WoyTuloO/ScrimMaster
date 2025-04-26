package com.woytuloo.ScrimMaster.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void getUsers() {

    }

    @Test
    void getCurrentUser() {


    }

    @Test
    void addUser() throws Exception {
        User user1 = new User("UserOne", "password1", "userone@example.com");
        user1.setKd(1.2);
        user1.setAdr(2.3);
        user1.setRanking(10);
        user1.setPersmissionLevel(1);

        String userJson = objectMapper.writeValueAsString(user1);

        String responseJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User returnedUser = objectMapper.readValue(responseJson, User.class);

        assertThat(returnedUser.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}