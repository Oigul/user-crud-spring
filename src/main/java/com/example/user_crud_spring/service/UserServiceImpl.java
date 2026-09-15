package com.example.user_crud_spring.service;

import com.example.user_crud_spring.dtos.UserCreateRequest;
import com.example.user_crud_spring.dtos.UserResponse;
import com.example.user_crud_spring.dtos.UserUpdateRequest;
import com.example.user_crud_spring.kafka.events.UserEvent;
import com.example.user_crud_spring.mappers.UserMapper;
import com.example.user_crud_spring.model.User;
import com.example.user_crud_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "user-events";

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists: " + request.getEmail());
        }

        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        sendEventToKafka(savedUser.getEmail(), "CREATE");

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        userRepository.save(user);
        return UserMapper.toResponse(user);
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
