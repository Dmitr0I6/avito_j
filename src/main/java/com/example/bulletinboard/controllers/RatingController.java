package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.response.RatingResponse;
import com.example.bulletinboard.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
@Tag(name = "Сервис оценки пользователей", description = "API для работы с пользовательскми отзывами")
public class RatingController {

    private final RatingService ratingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-rate")
    @Operation(summary = "Создание отзыва на пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Рейтинг успешно поставлен"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void addRating(@Valid @RequestBody RatingRequest ratingRequest) {
         ratingService.rateUser(ratingRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/current")
    @Operation(summary = "Получение отзывов текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Рейтинг получен"),
            @ApiResponse(responseCode = "400",description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<RatingResponse> getCurrentRate(){
        return ratingService.getCurrentUserRate();
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление отзыва для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Рейтинг получен"),
            @ApiResponse(responseCode = "400",description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void deleteRating(@PathVariable Long id){
        ratingService.deleteRating(id);
    }



    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("{id}")
    @Operation(summary = "Получение отзывов пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Рейтинг получен"),
            @ApiResponse(responseCode = "400",description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<RatingResponse> getUserRating(@PathVariable String id){
        return ratingService.getUserRating(id);
    }



}
