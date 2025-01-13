package com.ondo.ondo_back.auth.jwt;

import com.ondo.ondo_back.auth.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        // 로그아웃 경로, 메서드 확인
        String requestURI = request.getRequestURI();

        if (!"/logout".equals(requestURI) || !"POST".equals(request.getMethod())) {

            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 리프레쉬 토큰 가져오기
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

        // 리프레쉬 토큰 유효성 검사
        if (refresh == null || !"refresh".equals(jwtUtil.getCategory(refresh)) || !refreshRepository.existsByRefreshToken(refresh)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("유효하지 않은 리프레쉬 토큰입니다.");
            return;
        }

        // DB에서 리프레쉬 토큰 제거
        refreshRepository.deleteByEmail(jwtUtil.getEmail(refresh));

        // 리프레쉬 토큰 쿠키에서 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("성공적으로 로그아웃 되었습니다.");
    }
}
