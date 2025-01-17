package com.ondo.ondo_back.common.dto;

import lombok.Data;

@Data
public class ReviewResponseDto {

    private int reviewId;
    private int targetType;
    private int targetId;
    private int memberId;
    private int rating;
    private String content;
    private String createdAt;
    private String updatedAt;
}
