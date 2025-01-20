package com.ondo.ondo_back.recommendation.dto;

import com.ondo.ondo_back.common.dto.ReviewResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RestaurantsRecommendResponseDto {

    private String name;
    private String category;
    private String address;
    private Map<String, String> menu;
    private List<ReviewResponseDto> reviews;
}
