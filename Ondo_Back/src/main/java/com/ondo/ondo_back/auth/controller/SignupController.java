package com.ondo.ondo_back.auth.controller;

import com.ondo.ondo_back.auth.dto.SignupRequestDto;
import com.ondo.ondo_back.auth.dto.SignupResponseDto;
import com.ondo.ondo_back.auth.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {

        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signupProcess(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {

        Integer memberId = signupService.signupProcess(signupRequestDto);
        return ResponseEntity.ok(new SignupResponseDto(memberId));
    }
}
