package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.*;
import com.example.bulletinboard.response.AuthResponse;
import com.example.bulletinboard.response.UserResponse;
import com.example.bulletinboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "Сервис пользователей", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Потльзователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }


    @Operation(summary = "Вход в систему")
    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse getUserById(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }


    @Operation(summary = "Обновление токена")
    @PostMapping("/refresh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return userService.refreshAccessToken(refreshRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Получение пользователя по ID")
    @GetMapping("{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользовалель удален"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable String id){
        userService.deleteUser(id);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PatchMapping("/update-password")
    @Operation(summary = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользовалель удален"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void updateUserAuth(
             @Valid @RequestBody UserAuthUpdateRequest userUpdateAuth){
        userService.updateUserAuth(userUpdateAuth);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-users")
    @Operation(summary = "Получение всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользовалель удален"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return userService.findAllUsers();
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PatchMapping("/update-info")
    @Operation(summary = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользовалель удален"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUserInfo(
            @Valid @RequestBody UserInfoUpdateRequest userUpdateInfo){
        return userService.updateUserInfo(userUpdateInfo);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createmoderator")
    @Operation(summary = "Создание пользователя с правами модератора")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользователь создан"),
            @ApiResponse(responseCode = "400",description = "Ошибка, проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse createModerator(@Valid @RequestBody UserRequest userRequest){
        return userService.createModerator(userRequest);
    }
}
