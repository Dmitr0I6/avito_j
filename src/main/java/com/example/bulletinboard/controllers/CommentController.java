package com.example.bulletinboard.controllers;

import com.example.bulletinboard.request.CommentRequest;
import com.example.bulletinboard.request.CommentUpdateRequest;
import com.example.bulletinboard.response.CommentResponse;
import com.example.bulletinboard.service.CommentService;
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
@RequestMapping("/api/comment")
@Tag(name = "Сервис комментариев", description = "API для работы с комментариями")
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-comment")
    @Operation(summary = "Создание комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно оставлен"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void addRating(@RequestBody CommentRequest commentRequest) {
        commentService.createComment(commentRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/current")
    @Operation(summary = "Получение комментариев текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCurrentUserComments() {
        return commentService.getAllCommentsByUser();
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @GetMapping("/{adId}")
    @Operation(summary = "Получение комментариев текущего объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCommentsByAd(@PathVariable Long adId) {
        return commentService.getAllCommentsByAdId(adId);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление комментариев")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий уделен"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteCommentById(id);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    @PatchMapping("/update/{id}")
    @Operation(summary = "Редактирование комментариев")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий обновлен"),
            @ApiResponse(responseCode = "400", description = "Проверьте введенные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка работы сервиса")
    })
    @ResponseStatus(HttpStatus.OK)
    public void updateComment(@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.updateCommentById(id, commentUpdateRequest);
    }


}
