package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.mapper.AdvertisementMapper;
import com.example.bulletinboard.repository.AdvertisementRepository;
import com.example.bulletinboard.response.AdvertisementResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;


}
