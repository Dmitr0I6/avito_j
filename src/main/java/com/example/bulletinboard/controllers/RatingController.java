package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/createuser")
    @Operation(summary = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Рейтинг успешно поставлен"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500",description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void addRating(@RequestBody RatingRequest ratingRequest) {
         //ratingService.rateUser(ratingRequest);
    }



}
