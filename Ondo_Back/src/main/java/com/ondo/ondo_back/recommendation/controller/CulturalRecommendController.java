package com.ondo.ondo_back.recommendation.controller;

import com.ondo.ondo_back.recommendation.dto.CulturalRecommendRequestDto;
import com.ondo.ondo_back.recommendation.dto.CulturalRecommendResponseDto;
import com.ondo.ondo_back.recommendation.service.CulturalRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class CulturalRecommendController {

    private final CulturalRecommendService culturalRecommendService;

    @Autowired
    public CulturalRecommendController(CulturalRecommendService culturalRecommendService) {

        this.culturalRecommendService = culturalRecommendService;
    }

    @PostMapping
    public ResponseEntity<List<CulturalRecommendResponseDto>> getRecommendations(
            @RequestBody CulturalRecommendRequestDto culturalRecommendRequestDto) {

        List<CulturalRecommendResponseDto> recommendations = culturalRecommendService.getRecommendations(culturalRecommendRequestDto);
        return ResponseEntity.ok(recommendations);
    }
}
