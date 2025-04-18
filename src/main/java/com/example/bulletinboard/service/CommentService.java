package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Comment;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.CommentMapper;
import com.example.bulletinboard.repository.CommentRepository;
import com.example.bulletinboard.request.CommentRequest;
import com.example.bulletinboard.request.CommentUpdateRequest;
import com.example.bulletinboard.response.CommentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final AdvertisementService advertisementService;

    @Transactional
    public List<CommentResponse> getAllCommentsByAdId(Long id) {
        if (!advertisementService.existWithId(id)) {
            throw new ResourceNotFoundException("Ad does not exist");
        }
        List<Comment> commentList = commentRepository.getCommentsByAdvertisementId(id);
        return commentMapper.toCommentResponses(commentList);
    }


    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional
    public void createComment(CommentRequest commentRequest) {
        Advertisement advertisement = advertisementService.
                getAdvertisementEntityById(commentRequest.getAdvertisement());

        Comment comment = commentMapper.toComment(commentRequest, advertisement);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(userService.getCurrentUser());

        commentRepository.save(comment);
    }

    @Transactional
    public List<CommentResponse> getAllCommentsByUser() {
        List<Comment> comments = commentRepository.getCommentsByAuthorId(
                userService.getCurrentUserId()
        );
        return commentMapper.toCommentResponses(comments);
    }

    public void deleteCommentById(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        if (comment.getAuthor().getId().equals(userService.getCurrentUserId()) || userService.isAdminOrModerator()
        ) {
            commentRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Fail to delete comment");
        }
    }


    public CommentResponse updateCommentById(Long id, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(id).
                orElseThrow(()->{return new ResourceNotFoundException("Comment not found with id:" + id);}
                );
        comment.setText(commentUpdateRequest.getText());
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }
}
