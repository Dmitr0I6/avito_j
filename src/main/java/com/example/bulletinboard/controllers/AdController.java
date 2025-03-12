package com.example.bulletinboard.controllers;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.service.AdvertismentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/advertisments")
@Tag(name = "Сервис объявлений",description = "API для работы с объявлениями")
public class AdController {

    AdvertismentService advertismentService;

    AdController(AdvertismentService advertismentService) {
        this.advertismentService = advertismentService;
    }

    @GetMapping
    @Operation(summary = "Получение списка объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public List<Advertisement> getAdvertisments()
    {
        return advertismentService.getAllAdvertisments();
    }


    @PostMapping
    @Operation(summary = "Создание объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление создано"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    public Advertisement createAdvertisment(@RequestBody Advertisement advertisment){
        return advertismentService.saveAdvertisment(advertisment);
    }


}
