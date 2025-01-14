package com.ondo.ondo_back.common.controller;

import com.ondo.ondo_back.auth.dto.MemberInfoDto;
import com.ondo.ondo_back.auth.dto.MemberUpdateDto;
import com.ondo.ondo_back.common.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {

        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<?> getMemberInfo(@RequestParam(required = false) Integer memberId) {

        try {

            if (memberId == null) {

                throw new IllegalArgumentException("회원 아이디가 null입니다.");
            }

            MemberInfoDto memberInfo = memberService.getMemberInfo(memberId);
            return ResponseEntity.ok(memberInfo);
        } catch (IllegalArgumentException e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원 정보를 불러오는 과정에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateMemberInfo(
            @RequestParam(required = false) Integer memberId,
            @RequestBody MemberUpdateDto memberUpdateDto) {

        try {

            if (memberId == null) {

                throw new IllegalArgumentException("회원 아이디가 null입니다.");
            }

            if (memberUpdateDto == null) {

                throw new IllegalArgumentException("회원 정보 변경 사항이 null입니다.");
            }

            MemberInfoDto updateMemberInfo = memberService.updateMemberInfo(memberId, memberUpdateDto);
            return ResponseEntity.ok(updateMemberInfo);
        } catch (IllegalArgumentException e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원 정보를 불러오는 과정에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMemberInfo(@RequestParam(required = false) Integer memberId) {

        try {

            if (memberId == null) {

                throw new IllegalArgumentException("회원 아이디가 null입니다.");
            }
            memberService.deleteMemberInfo(memberId);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("success", "회원 삭제 완료");
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "회원 정보를 불러오는 과정에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
