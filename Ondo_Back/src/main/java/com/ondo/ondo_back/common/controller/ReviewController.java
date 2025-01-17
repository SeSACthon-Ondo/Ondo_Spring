package com.ondo.ondo_back.common.controller;

import com.ondo.ondo_back.common.dto.ReviewRequestDto;
import com.ondo.ondo_back.common.dto.ReviewResponseDto;
import com.ondo.ondo_back.common.dto.ReviewUpdateRequestDto;
import com.ondo.ondo_back.common.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto reviewRequestDto
    ) {

        return ResponseEntity.ok(reviewService.createReview(reviewRequestDto));
    }

    // 리뷰 조회
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews(
            @RequestParam int targetType,
            @RequestParam int targetId
    ) {

        return ResponseEntity.ok(reviewService.getReviews(targetType, targetId));
    }

    // 리뷰 수정
    @PatchMapping
    public ResponseEntity<ReviewResponseDto> updateReview(
            @RequestParam int reviewId,
            @RequestParam int memberId,
            @RequestBody ReviewUpdateRequestDto reviewUpdateRequestDto
    ) {

        ReviewResponseDto reviewUpdateResponseDto = reviewService.updateReview(reviewId, memberId, reviewUpdateRequestDto);
        return ResponseEntity.ok(reviewUpdateResponseDto);
    }

    // 리뷰 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteReview(
            @RequestParam int reviewId
    ) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
