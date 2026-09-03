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
    public boolean createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("existing email address: {}", userDTO.getEmail());
            return false;
        }

        User user = UserMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);

        sendEventToKafka(savedUser.getEmail(), "CREATE");

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

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("existing email address: {}", userDTO.getEmail());
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
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return false;
        }

        userRepository.deleteById(id);

        sendEventToKafka(user.getEmail(), "DELETE");
        return true;
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
