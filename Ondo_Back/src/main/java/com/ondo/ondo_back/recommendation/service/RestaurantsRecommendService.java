package com.ondo.ondo_back.recommendation.service;

import com.ondo.ondo_back.common.repository.RestaurantsRepository;
import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendRequestDto;
import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantsRecommendService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestaurantsRepository restaurantsRepository;

    public RestaurantsRecommendService(RestaurantsRepository restaurantsRepository) {

        this.restaurantsRepository = restaurantsRepository;
    }

    public List<RestaurantsRecommendResponseDto> getRecommendations(RestaurantsRecommendRequestDto restaurantsRecommendRequestDto) {

        String userLocation = restaurantsRecommendRequestDto.getUserLocation();
        String foodsQuery = restaurantsRecommendRequestDto.getFoodsQuery();
    }
}
