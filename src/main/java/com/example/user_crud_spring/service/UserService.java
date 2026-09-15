package com.example.user_crud_spring.service;

import com.example.user_crud_spring.dtos.UserCreateRequest;
import com.example.user_crud_spring.dtos.UserResponse;
import com.example.user_crud_spring.dtos.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
