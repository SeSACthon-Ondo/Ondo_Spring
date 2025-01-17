package com.ondo.ondo_back.auth.service;

import com.ondo.ondo_back.auth.dto.LoginResponseDto;
import com.ondo.ondo_back.auth.dto.MemberInfoDto;
import com.ondo.ondo_back.auth.entity.RefreshToken;
import com.ondo.ondo_back.auth.jwt.JWTUtil;
import com.ondo.ondo_back.auth.repository.RefreshRepository;
import com.ondo.ondo_back.common.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    public LoginService(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            RefreshRepository refreshRepository,
            MemberRepository memberRepository
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.memberRepository = memberRepository;
    }

    public LoginResponseDto login(String email, String password) {

        Optional<com.ondo.ondo_back.common.entity.Member> optionalMemberEntity = memberRepository.findByEmail(email);

        com.ondo.ondo_back.common.entity.Member memberEntity = optionalMemberEntity
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."));

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String accessToken = jwtUtil.createJwt("access", email, role, 864000000L);
            String refreshToken = getOrCreateRefreshToken(email, role);

            MemberInfoDto memberInfo = new MemberInfoDto(
                    memberEntity.getMemberId(),
                    memberEntity.getEmail(),
                    memberEntity.getNickname(),
                    memberEntity.getRole()
            );

            return new LoginResponseDto(accessToken, refreshToken, memberInfo);
        } catch (BadCredentialsException e) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }
    }

    private String getOrCreateRefreshToken(String email, String role) {

        Optional<RefreshToken> existingRefreshToken = refreshRepository.findByEmail(email);

        if (existingRefreshToken.isPresent() && !jwtUtil.isExpired(existingRefreshToken.get().getRefreshToken())) {

            return existingRefreshToken.get().getRefreshToken();
        }

        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, 8640000000L);

        addRefreshEntity(email, newRefreshToken, 8640000000L);
        return newRefreshToken;
    }

    private void addRefreshEntity(String email, String refreshToken, long expiredMs) {

        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

        refreshRepository.deleteByEmail(email);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setEmail(email);
        refreshTokenEntity.setExpiration(expirationDate.toString());

        refreshRepository.save(refreshTokenEntity);
    }
}
