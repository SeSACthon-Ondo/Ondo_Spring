package com.ondo.ondo_back.recommendation.dto;

import lombok.Data;

@Data
public class RestaurantsRecommendRequestDto {

    private String userLocation;
    private String foodsQuery;
}
