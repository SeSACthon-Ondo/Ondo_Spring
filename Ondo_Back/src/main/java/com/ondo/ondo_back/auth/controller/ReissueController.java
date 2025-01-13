package com.ondo.ondo_back.auth.controller;

import com.ondo.ondo_back.auth.entity.RefreshToken;
import com.ondo.ondo_back.auth.jwt.JWTUtil;
import com.ondo.ondo_back.auth.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping("/reissue")
    public ResponseEntity<Map<String, String>> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 리프레쉬 토큰 추출
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            for (Cookie cookie : cookies) {

                if ("refresh".equals(cookie.getName())) {

                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createResponse("실패", "리프레쉬 토큰이 없습니다."));
        }

        // 리프래쉬 토큰 만료 여부 확인
        try {

            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createResponse("실패", "리프레쉬 토큰이 만료되었습니다."));
        }

        // 토큰 종류 확인
        String category = jwtUtil.getCategory(refresh);

        if (!"refresh".equals(category)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createResponse("실패", "유효하지 않은 리프레쉬 토큰입니다."));
        }

        // DB에 리프레쉬 토큰이 존재하는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);

        if (isExist == null || !isExist) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createResponse("실패", "존재하지 않는 리프레쉬 토큰입니다."));
        }

        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새 토큰 생성
        String newAccess = jwtUtil.createJwt("access", email, role, 864000000L);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, 8640000000L);

        // 새 토큰 DB에 저장
        refreshRepository.deleteByEmail(email);
        addRefreshEntity(email, newRefresh, 8640000000L);

        // 응답 헤더와 쿠키 설정
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("status", "success");
        successResponse.put("message", "토큰이 성공적으로 발급되었습니다.");
        successResponse.put("accessToken", newAccess);
        successResponse.put("refreshToken", newRefresh);

        return ResponseEntity.ok(successResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String email, String newRefresh, long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setRefreshToken(newRefresh);
        refreshEntity.setEmail(email);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Map<String, String> createResponse(String status, String message) {

        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        return response;
    }
}
