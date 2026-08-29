package com.example.user_crud_spring.controller;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //POST http://localhost:8080/api/users
    @PostMapping
    public boolean createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    //GET http://localhost:8080/api/users/5
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    //GET http://localhost:8080/api/users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    //PUT http://localhost:8080/api/users
    @PutMapping
    public boolean updateUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    //DELETE http://localhost:8080/api/users/5
    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
