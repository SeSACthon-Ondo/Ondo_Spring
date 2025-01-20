package com.ondo.ondo_back.recommendation.dto;

import lombok.Data;

@Data
public class RestaurantsRecommendRequestDto {

    private String district;
    private double latitude;
    private double longitude;
    private String foodsQuery;
}
