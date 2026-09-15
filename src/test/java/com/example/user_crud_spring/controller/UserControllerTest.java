package com.example.user_crud_spring.controller;

import com.example.user_crud_spring.assembler.UserModelAssembler;
import com.example.user_crud_spring.dtos.UserCreateRequest;
import com.example.user_crud_spring.dtos.UserResponse;
import com.example.user_crud_spring.dtos.UserUpdateRequest;
import com.example.user_crud_spring.service.UserService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserModelAssembler.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateRequest testCreateRequest;
    private UserUpdateRequest testUpdateRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        testCreateRequest = new UserCreateRequest("Анна", "anna@example.com", 55);
        testUpdateRequest = new UserUpdateRequest("Анна", "anna@example.com", 55);
        testResponse = new UserResponse(1L, "Анна", "anna@example.com", 55);
    }

    @Test
    void createUser_Successfully() throws Exception {
        Mockito.when(userService.createUser(any(UserCreateRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(55));
    }

    @Test
    void createUser_NotSuccessfully() throws Exception {
        Mockito.when(userService.createUser(any(UserCreateRequest.class)))
                .thenThrow(new IllegalStateException("Email already exists: " + testCreateRequest.getEmail()));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_InvalidData() throws Exception {
        UserCreateRequest invalidUser = new UserCreateRequest("", "bad email", -55);

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
        Mockito.when(userService.getUserById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(55));
    }

    @Test
    void getUserById_NotSuccessfully() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Successfully() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(testResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResponseList.length()").value(1))
                .andExpect(jsonPath("$._embedded.userResponseList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.userResponseList[0].name").value("Анна"));
    }

    @Test
    void getAllUsers_EmptyList() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void updateUser_Successfully() throws Exception {
        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(testResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анна"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(55));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        Mockito.when(userService.updateUser(eq(99L), any(UserUpdateRequest.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_EmailConflict() throws Exception {
        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenThrow(new IllegalStateException("Email already exists: " + testUpdateRequest.getEmail()));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_InvalidData() throws Exception {
        UserUpdateRequest invalidUser = new UserUpdateRequest("", "bad email", -55);

        mockMvc.perform(put("/api/users/1")
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

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_Successfully() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NotSuccessfully() throws Exception {
        Mockito.doThrow(new NoSuchElementException())
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
