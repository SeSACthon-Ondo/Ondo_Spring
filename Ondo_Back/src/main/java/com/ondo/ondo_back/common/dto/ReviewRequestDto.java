package com.ondo.ondo_back.common.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {

    private int memberId;
    private int targetType;
    private int targetId;
    private int rating;
    private String content;
}
