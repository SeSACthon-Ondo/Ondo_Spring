package com.ondo.ondo_back.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId", nullable = false)
    private int reviewId;

    @Column(name = "memberId", nullable = false)
    private int memberId;

    @Column(name = "targetType", nullable = false)
    private int targetType;

    @Column(name = "targetId", nullable = false)
    private int targetId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }
}
