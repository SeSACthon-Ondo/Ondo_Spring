package com.ondo.ondo_back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {

    private Integer memberId;
    private String email;
    private String nickname;
    private String role;
}
