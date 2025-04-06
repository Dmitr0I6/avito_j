package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.UserResponse;
import com.example.bulletinboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "Сервис пользователей", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Создание пользователя", security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Потльзователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
    }


    @Operation(summary = "Получение пользователя по ID")
    @GetMapping("{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользовалель удален"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
