package com.ondo.ondo_back.recommendation.dto;

import lombok.Data;

@Data
public class CulturalRecommendRequestDto {

    private String userLocation;
    private String productQuery;
}
