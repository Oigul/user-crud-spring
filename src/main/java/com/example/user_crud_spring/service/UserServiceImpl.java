package com.example.user_crud_spring.service;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.kafka.events.UserEvent;
import com.example.user_crud_spring.mappers.UserMapper;
import com.example.user_crud_spring.model.User;
import com.example.user_crud_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "user-events";

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already exists: " + userDTO.getEmail());
        }

        User user = UserMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);

        sendEventToKafka(savedUser.getEmail(), "CREATE");

        return UserMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User id must not be null");
        }

        User user = userRepository.findById(userDTO.getId()).orElseThrow(NoSuchElementException::new);

        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already exists: " + userDTO.getEmail());
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());

        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);

        userRepository.deleteById(id);

        sendEventToKafka(user.getEmail(), "DELETE");
    }

    private void sendEventToKafka(String email, String operation) {
        try {
            UserEvent event = new UserEvent(email, operation);

            String jsonMessage = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, jsonMessage);

            log.info("Successfully sent event to Kafka: {}", jsonMessage);
        } catch (Exception e) {
            log.error("Error sending event to Kafka", e);
        }
    }
}
