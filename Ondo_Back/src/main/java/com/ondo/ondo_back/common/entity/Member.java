package com.ondo.ondo_back.common.entity;

import com.ondo.ondo_back.auth.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId", nullable = false)
    private int memberId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "role", nullable = false)
    private String role;

    public static Member of(SignupRequestDto requestDto, String encodedPassword) {

        return new Member(

                0,
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getNickname(),
                "USER"
        );
    }
}
