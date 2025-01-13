package com.ondo.ondo_back.common.repository;

import com.ondo.ondo_back.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    // 이메일이 존재하는지 확인
    Boolean existsByEmail(String email);

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);
}
