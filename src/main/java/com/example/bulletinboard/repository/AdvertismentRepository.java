package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertismentRepository extends JpaRepository<Advertisement, Long> {

}
