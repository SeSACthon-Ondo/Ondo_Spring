package com.ondo.ondo_back.auth.jwt;

import com.ondo.ondo_back.auth.dto.MemberDetails;
import com.ondo.ondo_back.auth.service.TokenBlacklistService;
import com.ondo.ondo_back.common.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public JWTFilter(JWTUtil jwtUtil, MemberRepository memberRepository, TokenBlacklistService tokenBlacklistService) {

        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // /signup, /login 경로는 패스
        if ("/signup".equals(requestURI) || "/login".equals(requestURI)) {

            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 AccessToken 꺼내기
        String accessToken = request.getHeader("Authorization").substring(7);;

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 블랙리스트에 있는지 확인
        if (tokenBlacklistService.isBlacklisted(accessToken)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print("{\"error\": \"블랙리스트 토큰\", \"message\": \"해당 토큰은 블랙리스트에 포함되어 있습니다.\"}");
            return;
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print("{\"error\": \"토큰 만료\", \"message\": \"Access Token이 만료되었습니다.\"}");
            return;
        }

        // 토큰이 access 인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!"access".equals(category)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print("{\"error\": \"유효하지 않은 토큰\", \"message\": \"유효하지 않은 Access Token입니다.\"}");
            return;
        }

        // 이메일 값 획득 후 사용자 조회
        String email = jwtUtil.getEmail(accessToken);
        Optional<com.ondo.ondo_back.common.entity.Member> memberEntityOptional = memberRepository.findByEmail(email);

        if (memberEntityOptional.isEmpty()) {

            throw new IllegalArgumentException("사용자를 찾을 수 없습니다,");
        }

        com.ondo.ondo_back.common.entity.Member memberEntity = memberEntityOptional.get();

        // 사용자 권한 설정
        MemberDetails memberDetails = new MemberDetails(memberEntity);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                memberDetails, null, memberDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
