package com.ondo.ondo_back.auth.controller;

import com.ondo.ondo_back.auth.dto.LoginRequestDto;
import com.ondo.ondo_back.auth.dto.LoginResponseDto;
import com.ondo.ondo_back.auth.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {

        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<Object> login(
            @RequestBody LoginRequestDto loginRequestDto
            ) {

        try {
            LoginResponseDto response = loginService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getReason());
            errorResponse.put("status", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "예상치 못한 에러 발생");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
