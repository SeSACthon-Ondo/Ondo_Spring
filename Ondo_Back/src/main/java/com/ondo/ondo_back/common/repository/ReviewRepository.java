package com.ondo.ondo_back.common.repository;

import com.ondo.ondo_back.common.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByTargetTypeAndTargetId(int targetType, int targetId);

    Optional<Review> findByReviewIdAndMemberId(int reviewId, int memberId);
}
