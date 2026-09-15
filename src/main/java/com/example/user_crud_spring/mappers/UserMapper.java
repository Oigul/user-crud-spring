package com.example.user_crud_spring.mappers;

import com.example.user_crud_spring.dtos.UserCreateRequest;
import com.example.user_crud_spring.dtos.UserResponse;
import com.example.user_crud_spring.model.User;

public class UserMapper {
        public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }

    public static User toEntity(UserCreateRequest request) {
        return new User(request.getName(), request.getEmail(), request.getAge());
    }

}
