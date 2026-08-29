package com.example.user_crud_spring.controller;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.service.UserService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO(1L, "Анна", "anna@example.com", 55);
    }

    @Test
    void сreateUser_Successfully() throws Exception {
        Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void createUser_NotSuccessfully() throws Exception {
        Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void createUser_InvalidData() throws Exception {
        UserDTO invalidUser = new UserDTO(null, "", "bad email", -55);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void createUser_InvalidJson() throws Exception {
        String invalidJson = """
            {
                "name": "Анна",
                "email": "anna@example.com",
                "age": 55
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getUserById_Successfully() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(55));
    }

    @Test
    void getUserById_NotSuccessfully() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getAllUsers_Successfully() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Анна"));
    }

    @Test
    void getAllUsers_EmptyList() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateUser_Successfully() throws Exception {
        Mockito.when(userService.updateUser(any(UserDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void updateUser_NotSuccessfully() throws Exception {
        Mockito.when(userService.updateUser(any(UserDTO.class))).thenReturn(false);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void updateUser_InvalidData() throws Exception {
        UserDTO invalidUser = new UserDTO(1L, "", "bad email", -55);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void updateUser_InvalidJson() throws Exception {
        String invalidJson = """
            {
                "name": "Анна",
                "email": "anna@example.com",
                "age": 55
            """;

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_Successfully() throws Exception {
        Mockito.when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteUser_NotSuccessfully() throws Exception {
        Mockito.when(userService.deleteUser(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
