package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.AdvertisementRequest;
import com.example.bulletinboard.response.AdvertisementResponse;
import com.example.bulletinboard.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/advertisement")
@Tag(name = "Сервис объявлений", description = "API для работы с объявлениями")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping("page")
    @Operation(summary = "Получение страницы объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены"),
            @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<AdvertisementResponse> getAdvertisementPages(
            @Parameter(description = "Количество элементов на странице")
            @RequestParam(value = "limit", required = false) @Positive Integer limit) {
        return advertisementService.getAdvertisementList(limit);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/current-user")
    @Operation(summary = "Получение объявлений текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены"),
            @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<AdvertisementResponse> getAdvertisementsCurrentUser(){
        return advertisementService.getAdvertisementsCurrentUser();
    }


    @PostMapping(path = "createad",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создание объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление создано"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public void createAdvertisement(@ModelAttribute AdvertisementRequest advertisementRequest) {
        advertisementService.createAdvertisement(advertisementRequest, advertisementRequest.getImages());
    }

    @GetMapping(value = "{id}",
            produces = {
            MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Поиск объявления по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление найдено"),
            @ApiResponse(responseCode = "400", description = "Неверно переданы данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)

    public AdvertisementResponse getAdvertisementById(@PathVariable @PositiveOrZero Long id) {
        return advertisementService.getAdvertisementById(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удаление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех"),
            @ApiResponse(responseCode = "400", description = "Неверно переданные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdvertisementById(
            @Parameter(description = "ID объявления")
            @PathVariable @PositiveOrZero Long id) {
        advertisementService.deleteAdvertisementById(id);
    }

    @PatchMapping(value = "update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @ResponseStatus(HttpStatus.OK)
    public void updateAdvertisement(
            @Parameter(description = "ID объявления")
            @PathVariable @PositiveOrZero Long id,

            @Parameter(description = "Данные для обновления объявления")
            @ModelAttribute AdvertisementRequest advertisementRequest) {

        advertisementService.updateAdvertisement(id, advertisementRequest, advertisementRequest.getImages());
    }
}


