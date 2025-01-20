package com.ondo.ondo_back.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
