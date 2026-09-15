package com.example.user_crud_spring.controller;

import com.example.user_crud_spring.assembler.UserModelAssembler;
import com.example.user_crud_spring.dtos.UserCreateRequest;
import com.example.user_crud_spring.dtos.UserResponse;
import com.example.user_crud_spring.dtos.UserUpdateRequest;
import com.example.user_crud_spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление профилями пользователей")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать пользователя", description = "Проверяет уникальность email, валидирует DTO и отправляет уведомление в Kafka. Возвращает созданного пользователя.")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по его ID", description = "Возвращает пользователя вместе с навигационными HATEOAS ссылками")
    public EntityModel<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return assembler.toModel(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей", description = "Возвращает массив всех пользователей. Каждый элемент содержит персональную ссылку.")
    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return assembler.toCollectionModel(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя", description = "Обновляет имя, возраст или email существующего пользователя по его ID. Возвращает обновлённые данные пользователя.")
    public UserResponse  updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID и отправляет уведомление в Kafka.")
    public void  deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
