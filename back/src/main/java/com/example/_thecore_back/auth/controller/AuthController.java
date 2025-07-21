package com.example._thecore_back.auth.controller;

import com.example._thecore_back.auth.application.TokenService;
import com.example._thecore_back.auth.domain.JwtTokenProvider;
import com.example._thecore_back.common.dto.ApiResponse;
import com.example._thecore_back.auth.domain.LoginRequest;
import com.example._thecore_back.auth.domain.RefreshRequest;
import com.example._thecore_back.auth.domain.TokenDto;
import com.example._thecore_back.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest request) {
        TokenDto tokenDto = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("토큰이 존재하지 않습니다."));
        }

        String token = authHeader.substring(7);

        long expiration = jwtTokenProvider.getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        if (expiration <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("이미 만료된 토큰입니다."));
        }

        tokenService.blacklistAccessToken(token, expiration);

        return ResponseEntity.ok((ApiResponse<Void>) ApiResponse.successWithNoData("로그아웃 처리 완료"));



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
