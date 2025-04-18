package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query(value = "SELECT * FROM advertisement ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Advertisement> findLastAds(@Param("limit") int limit);

    @Query(value = "SELECT * FROM advertisement WHERE user_id = :id ORDER BY created_at DESC ", nativeQuery = true)
    List<Advertisement> findAllByUserId(@Param("id") String id);
}
