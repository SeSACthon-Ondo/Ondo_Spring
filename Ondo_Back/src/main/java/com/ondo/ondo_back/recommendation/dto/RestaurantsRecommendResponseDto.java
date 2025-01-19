package com.ondo.ondo_back.recommendation.dto;

import lombok.Data;

@Data
public class RestaurantsRecommendResponseDto {

    private String name;
    private String category;
    private String address;
    private String menu;
    private String review;
}
