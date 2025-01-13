package com.ondo.ondo_back.auth.controller;

import com.ondo.ondo_back.auth.jwt.JWTUtil;
import com.ondo.ondo_back.auth.repository.RefreshRepository;
import com.ondo.ondo_back.auth.service.TokenBlacklistService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth-logout")
public class LogoutController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public LogoutController(JWTUtil jwtUtil, RefreshRepository refreshRepository, TokenBlacklistService tokenBlacklistService) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String accessToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // Bearer prefix 제거
        if (accessToken != null && accessToken.startsWith("Bearer ")) {

            accessToken = accessToken.substring("Bearer ".length());
        } else {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "실패");
            errorResponse.put("message", "유효하지 않은 엑세스 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 엑세스 토큰을 블랙리스트에 추가
        long timeToExpire = jwtUtil.getExpiration(accessToken).getTime() - System.currentTimeMillis();
        tokenBlacklistService.addToBlacklist(accessToken, timeToExpire);

        // 쿠키에서 리프레쉬 토큰 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            for (Cookie cookie : cookies) {

                if ("refresh".equals(cookie.getName())) {

                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !"refresh".equals(jwtUtil.getCategory(refreshToken))) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "실패");
            errorResponse.put("message", "유효하지 않은 리프레쉬 토큰입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 리프레쉬 토큰이 DB에 있는지 확인
        if (!Boolean.TRUE.equals(refreshRepository.existsByRefresh(refreshToken))) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "실패");
            errorResponse.put("message", "존재하지 않는 리프레쉬 토큰입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // DB에서 리프레쉬 토큰 삭제
        refreshRepository.deleteByEmail(jwtUtil.getEmail(refreshToken));

        // 쿠키 만료시켜 삭제
        Cookie deleteCookie = new Cookie("refresh", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("status", "성공");
        successResponse.put("message", "로그아웃 되었습니다.");
        return ResponseEntity.ok(successResponse);
    }
}
