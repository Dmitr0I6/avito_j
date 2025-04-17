package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Comment;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.CommentMapper;
import com.example.bulletinboard.repository.CommentRepository;
import com.example.bulletinboard.request.CommentRequest;
import com.example.bulletinboard.request.CommentUpdateRequest;
import com.example.bulletinboard.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserService userService;

    @Mock
    private AdvertisementService advertisementService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void getAllCommentsByAdId_ShouldReturnCommentsWhenAdExists() {
        // Arrange
        Long adId = 1L;
        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();

        when(advertisementService.existWithId(adId)).thenReturn(true);
        when(commentRepository.getCommentsByAdvertisementId(adId)).thenReturn(List.of(comment));
        when(commentMapper.toCommentResponses(List.of(comment))).thenReturn(List.of(response));

        // Act
        List<CommentResponse> result = commentService.getAllCommentsByAdId(adId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(advertisementService).existWithId(adId);
        verify(commentRepository).getCommentsByAdvertisementId(adId);
    }

    @Test
    void getAllCommentsByAdId_ShouldThrowExceptionWhenAdNotExists() {
        // Arrange
        Long adId = 1L;
        when(advertisementService.existWithId(adId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getAllCommentsByAdId(adId));
    }

    @Test
    void getCommentById_ShouldReturnCommentWhenExists() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentResponse(comment)).thenReturn(response);

        // Act
        CommentResponse result = commentService.getCommentById(commentId);

        // Assert
        assertNotNull(result);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void getCommentById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(commentId));
    }

    @Test
    void createComment_ShouldSaveNewComment() {
        // Arrange
        CommentRequest request = new CommentRequest();
        request.setAdvertisement(1L);
        request.setText("Test comment");

        Advertisement ad = new Advertisement();
        User user = new User();
        Comment comment = new Comment();

        when(advertisementService.getAdvertisementEntityById(1L)).thenReturn(ad);
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentMapper.toComment(request, ad)).thenReturn(comment);

        // Act
        commentService.createComment(request);

        // Assert
        verify(commentRepository).save(comment);
        assertNotNull(comment.getCreatedAt());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    void getAllCommentsByUser_ShouldReturnUserComments() {
        // Arrange
        String userId = "userId";
        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();

        when(userService.getCurrentUserId()).thenReturn(userId);
        when(commentRepository.getCommentsByAuthorId(userId)).thenReturn(List.of(comment));
        when(commentMapper.toCommentResponses(List.of(comment))).thenReturn(List.of(response));

        // Act
        List<CommentResponse> result = commentService.getAllCommentsByUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).getCurrentUserId();
        verify(commentRepository).getCommentsByAuthorId(userId);
    }



    @Test
    void deleteCommentById_ShouldDeleteWhenUserIsAdmin() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        User user = new User();
        user.setId("admin");
        comment.setAuthor(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUserId()).thenReturn("userId");
        when(userService.isAdminOrModerator()).thenReturn(true);

        // Act
        commentService.deleteCommentById(commentId);

        // Assert
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void deleteCommentById_ShouldThrowExceptionWhenNotAllowed() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        User user = new User();
        user.setId("userId");
        comment.setAuthor(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUserId()).thenReturn("user");
        when(userService.isAdminOrModerator()).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> commentService.deleteCommentById(commentId));
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateCommentById_ShouldUpdateCommentText() {
        // Arrange
        Long commentId = 1L;
        CommentUpdateRequest updateRequest = new CommentUpdateRequest();
        updateRequest.setText("Updated text");

        Comment comment = new Comment();
        CommentResponse response = new CommentResponse();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentResponse(comment)).thenReturn(response);

        // Act
        CommentResponse result = commentService.updateCommentById(commentId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated text", comment.getText());
        verify(commentRepository).save(comment);
    }

}
