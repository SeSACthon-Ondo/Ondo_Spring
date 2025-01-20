package com.ondo.ondo_back.recommendation.controller;

import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendRequestDto;
import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendResponseDto;
import com.ondo.ondo_back.recommendation.service.RestaurantsRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations/restaurants")
public class RestaurantsRecommendController {

    private final RestaurantsRecommendService restaurantsRecommendService;

    public RestaurantsRecommendController(RestaurantsRecommendService restaurantsRecommendService) {

        this.restaurantsRecommendService = restaurantsRecommendService;
    }

    @PostMapping
    public ResponseEntity<List<RestaurantsRecommendResponseDto>> getRecommendations(
            @RequestBody RestaurantsRecommendRequestDto restaurantsRecommendRequestDto) {

        List<RestaurantsRecommendResponseDto> recommendations = restaurantsRecommendService.getRecommendations(restaurantsRecommendRequestDto);
        return ResponseEntity.ok(recommendations);
    }
}
