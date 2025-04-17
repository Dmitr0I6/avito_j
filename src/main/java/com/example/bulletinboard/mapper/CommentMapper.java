package com.example.bulletinboard.mapper;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Comment;
import com.example.bulletinboard.request.CommentRequest;
import com.example.bulletinboard.response.AdvertisementResponse;
import com.example.bulletinboard.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "advertisement", source = "advertisement")
    Comment toComment(CommentRequest commentRequest, Advertisement advertisement);

    @Mapping(target = "advertisement",source = "comment.advertisement.id")
    @Mapping(target = "user", source = "comment.author.id")
    CommentResponse toCommentResponse(Comment comment);

    List<CommentResponse> toCommentResponses(List<Comment> commentList);
}
