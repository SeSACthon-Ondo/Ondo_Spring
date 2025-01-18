package com.ondo.ondo_back.common.service;

import com.ondo.ondo_back.common.dto.ReviewRequestDto;
import com.ondo.ondo_back.common.dto.ReviewResponseDto;
import com.ondo.ondo_back.common.dto.ReviewUpdateRequestDto;
import com.ondo.ondo_back.common.entity.Review;
import com.ondo.ondo_back.common.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {

        this.reviewRepository = reviewRepository;
    }

    // 리뷰 저장
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto) {

        Review review = new Review();
        review.setMemberId(reviewRequestDto.getMemberId());
        review.setTargetType(reviewRequestDto.getTargetType());
        review.setTargetId(reviewRequestDto.getTargetId());
        review.setRating(reviewRequestDto.getRating());
        review.setContent(reviewRequestDto.getContent());

        Review savedReview = reviewRepository.save(review);
        return mapToDto(savedReview);
    }

    // 리뷰 조회
    public List<ReviewResponseDto> getReviews(int targetType, int targetId) {

        List<Review> reviews = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId);
        return reviews.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(int reviewId, int memberId, ReviewUpdateRequestDto reviewUpdateRequestDto) {

        Review review = reviewRepository.findByReviewIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));

        review.setContent(reviewUpdateRequestDto.getContent());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        return mapToDto(review);
    }

    // 리뷰 삭제
    public void deleteReview(int reviewId) {

        reviewRepository.deleteById(reviewId);
    }

    // Entity -> Dto
    private ReviewResponseDto mapToDto(Review review) {

        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setReviewId(review.getReviewId());
        dto.setTargetType(review.getTargetType());
        dto.setTargetId(review.getTargetId());
        dto.setMemberId(review.getMemberId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt().toString());
        dto.setUpdatedAt(review.getUpdatedAt().toString());

        return dto;
    }
}
