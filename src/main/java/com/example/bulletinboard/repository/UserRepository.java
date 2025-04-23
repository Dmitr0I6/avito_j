package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT user_id FROM users WHERE username = :username", nativeQuery = true)
    Optional<String> getIdByUsername(@Param ("username") String username);
}
