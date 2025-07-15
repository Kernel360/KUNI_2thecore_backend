package com.example._thecore_back.rest.auth.controller;

import com.example._thecore_back.common.dto.ApiResponse;
import com.example._thecore_back.rest.auth.domain.LoginRequest;
import com.example._thecore_back.rest.auth.domain.RefreshRequest;
import com.example._thecore_back.rest.auth.domain.TokenDto;
import com.example._thecore_back.rest.auth.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest request) {
        TokenDto tokenDto = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refresh(@RequestBody RefreshRequest request) {
        try {
            TokenDto tokenDto = authService.refresh(request);
            return ResponseEntity.ok(ApiResponse.success("엑세스 토큰 갱신 성공", tokenDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
