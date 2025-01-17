package com.ondo.ondo_back.auth.service;

import com.ondo.ondo_back.auth.dto.SignupRequestDto;
import com.ondo.ondo_back.common.entity.Member;
import com.ondo.ondo_back.common.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Integer signupProcess(SignupRequestDto signupRequestDto) {

        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        String confirmPassword = signupRequestDto.getConfirmPassword();
        String nickname = signupRequestDto.getNickname();

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {

            throw new DataIntegrityViolationException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 확인
        if (!password.equals(confirmPassword)) {

            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Member 엔티티 생성
        Member memberEntity = Member.of(signupRequestDto, encodedPassword);

        // DB에 저장
        Member savedMember = memberRepository.save(memberEntity);

        // 저장된 memberId 반환
        return savedMember.getMemberId();
    }
}
