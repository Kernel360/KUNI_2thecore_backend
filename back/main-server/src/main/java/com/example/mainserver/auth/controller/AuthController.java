package com.example.mainserver.auth.controller;

import com.example.common.dto.ApiResponse;
import com.example.mainserver.auth.application.TokenService;
import com.example.common.domain.auth.JwtTokenProvider;
import com.example.mainserver.auth.domain.LoginRequest;
import com.example.mainserver.auth.domain.RefreshRequest;
import com.example.mainserver.auth.domain.TokenDto;
import com.example.mainserver.auth.application.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증 토큰이 없습니다."));
        }

        String token = authHeader.substring(7);

        try {
            long expiration = jwtTokenProvider.getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
            if (expiration <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.fail("이미 만료된 토큰입니다."));
            }

            tokenService.blacklistAccessToken(token, expiration);
            return ResponseEntity.ok((ApiResponse<Void>) ApiResponse.successWithNoData("로그아웃 처리 완료"));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail("만료된 토큰입니다."));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail("유효하지 않은 토큰입니다."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refresh(@RequestBody RefreshRequest request) {
        TokenDto tokenDto = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("엑세스 토큰 갱신 성공", tokenDto));
    }
}