package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.repository.AdvertismentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertismentService {

    AdvertismentRepository advertismentRepository;

    AdvertismentService(AdvertismentRepository advertismentRepository) {
        this.advertismentRepository = advertismentRepository;
    }

    public Advertisement saveAdvertisment(Advertisement advertisment) {
        return advertismentRepository.save(advertisment);
    }

    public List<Advertisement> getAllAdvertisments() {
        return advertismentRepository.findAll();
    }
}
