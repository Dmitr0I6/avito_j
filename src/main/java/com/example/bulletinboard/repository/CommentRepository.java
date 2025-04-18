package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comments WHERE author_id = :author ORDER BY created_at DESC", nativeQuery = true)
    List<Comment> getCommentsByAuthorId(@Param("author") String authorId);

    @Query(value = "SELECT * FROM comments WHERE ad_id = :ad ORDER BY created_at", nativeQuery = true)
    List<Comment> getCommentsByAdvertisementId(@Param("ad") Long adId);

}
