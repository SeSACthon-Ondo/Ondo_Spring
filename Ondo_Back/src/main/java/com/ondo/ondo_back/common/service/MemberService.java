package com.ondo.ondo_back.common.service;

import com.ondo.ondo_back.auth.dto.MemberInfoDto;
import com.ondo.ondo_back.auth.dto.MemberUpdateDto;
import com.ondo.ondo_back.common.entity.Member;
import com.ondo.ondo_back.common.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public MemberInfoDto getMemberInfo(int memberId) {

        Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        return new MemberInfoDto(
                memberEntity.getMemberId(),
                memberEntity.getEmail(),
                memberEntity.getNickname(),
                memberEntity.getRole()
        );
    }

    @Transactional
    public MemberInfoDto updateMemberInfo(int memberId, MemberUpdateDto memberUpdateDto) {

        Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (memberUpdateDto.getPassword() != null) {

            String encodedPassword = bCryptPasswordEncoder.encode(memberUpdateDto.getPassword());
            memberEntity.setPassword(encodedPassword);
        }

        if (memberUpdateDto.getNickname() != null) {

            System.out.println("변경 전 닉네임: " + memberEntity.getNickname());
            memberEntity.setNickname(memberUpdateDto.getNickname());
            System.out.println("변경 후 닉네임: " + memberEntity.getNickname());
        }

        System.out.println("변경 후 닉네임: " + memberEntity.getNickname());
        memberRepository.save(memberEntity);

        return new MemberInfoDto(
                memberEntity.getMemberId(),
                memberEntity.getEmail(),
                memberEntity.getNickname(),
                memberEntity.getRole()
        );
    }

    @Transactional
    public void deleteMemberInfo(int memberId) {

        Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        memberRepository.delete(memberEntity);
    }
}
