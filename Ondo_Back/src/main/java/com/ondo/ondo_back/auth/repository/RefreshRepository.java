package com.ondo.ondo_back.auth.repository;

import com.ondo.ondo_back.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    // 리프레쉬 토큰이 존재하는지 확인
    Boolean existsByRefreshToken(String refreshToken);

    // 이메일로 리프레쉬 토큰 조회
    Optional<RefreshToken> findByEmail(String email);

    // 이메일로 리프레쉬 토큰 삭제
    @Transactional
    void deleteByEmail(String email);
}
