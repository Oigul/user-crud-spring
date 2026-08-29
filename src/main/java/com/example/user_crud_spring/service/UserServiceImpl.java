package com.example.user_crud_spring.service;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.mappers.UserMapper;
import com.example.user_crud_spring.model.User;
import com.example.user_crud_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean createUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            return false;
        }

        User user = userRepository.findById(userDTO.getId()).orElse(null);

        if (user == null) {
            return false;
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());

        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
