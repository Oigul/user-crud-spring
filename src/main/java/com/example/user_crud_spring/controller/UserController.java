package com.example.user_crud_spring.controller;

import com.example.user_crud_spring.dtos.UserDTO;
import com.example.user_crud_spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление профилями пользователей")
public class UserController {

    private final UserService userService;

    //POST http://localhost:8080/api/users

    @PostMapping
    @Operation(
            summary = "Создать пользователя",
            description = "Проверяет уникальность email, валидирует DTO и отправляет уведомление в Kafka. Возвращает true при успехе.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"name\": \"string\", \"email\": \"user@example.com\", \"age\": 0}"
                            )
                    )
            )
    )
    public boolean createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    //GET http://localhost:8080/api/users/5
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по его ID", description = "Возвращает DTO пользователя вместе с навигационными HATEOAS ссылками")
    public UserDTO getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);

        if (userDTO != null) {
            userDTO.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());

            userDTO.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        }

        return userDTO;
    }

    //GET http://localhost:8080/api/users
    @GetMapping
    @Operation(summary = "Получить список всех пользователей", description = "Возвращает массив всех пользователей. Каждый элемент содержит персональную ссылку.")
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();

        for (UserDTO userDTO : users) {
            userDTO.add(linkTo(methodOn(UserController.class).getUserById(userDTO.getId())).withSelfRel());
        }

        return users;
    }

    //PUT http://localhost:8080/api/users
    @PutMapping
    @Operation(summary = "Обновить данные пользователя", description = "Обновляет имя, возраст или email существующего пользователя по его ID. Возвращает true при успехе.")
    public boolean updateUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    //DELETE http://localhost:8080/api/users/5
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID и отправляет уведомление в Kafka. Возвращает true при успехе.")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
