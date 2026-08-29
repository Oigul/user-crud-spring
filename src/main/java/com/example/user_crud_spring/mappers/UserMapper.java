package com.example.user_crud_spring.mappers;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User(dto.getName(), dto.getEmail(), dto.getAge());
        user.setId(dto.getId());
        return user;
    }

}
