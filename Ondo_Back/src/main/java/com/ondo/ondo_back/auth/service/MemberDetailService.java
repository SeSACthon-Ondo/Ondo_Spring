package com.ondo.ondo_back.auth.service;

import com.ondo.ondo_back.auth.dto.MemberDetails;
import com.ondo.ondo_back.common.entity.Member;
import com.ondo.ondo_back.common.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailService(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Member> memberEntity = memberRepository.findByEmail(email);

        if (memberEntity.isEmpty()) {

            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        return new MemberDetails(memberEntity.get());
    }
}
