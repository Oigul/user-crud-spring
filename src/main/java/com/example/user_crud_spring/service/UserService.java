package com.example.user_crud_spring.service;

import com.example.user_crud_spring.dtos.UserDTO;

import java.util.List;

public interface UserService {
    boolean createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    boolean updateUser(UserDTO userDTO);
    boolean deleteUser(Long id);
}
