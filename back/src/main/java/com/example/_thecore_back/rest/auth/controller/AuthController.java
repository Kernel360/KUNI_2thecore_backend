package com.example._thecore_back.rest.auth.controller;

import com.example._thecore_back.common.dto.ApiResponse; // ApiResponse 추가
import com.example._thecore_back.rest.auth.jwt.JwtTokenProvider;
import com.example._thecore_back.rest.auth.model.LoginRequest;
import com.example._thecore_back.rest.auth.model.RefreshRequest;
import com.example._thecore_back.rest.auth.model.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest request) {
        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 인증된 사용자 정보
        String email = authentication.getName();

        // claims 생성
        Map<String, Object> claims = Map.of("email", email);
        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);
        LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(7);

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
        String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(accessExpireAt)
                .build();

        // ApiResponse를 활용해 일관된 응답 포맷으로 반환
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refresh(@RequestBody RefreshRequest request) {
        // 리프레시 토큰 검증
        ApiResponse<Boolean> validation = jwtTokenProvider.validateToken(request.getRefreshToken());
        if (!validation.isResult() || Boolean.FALSE.equals(validation.getData())) {
            // 실패 응답일 경우에도 동일한 응답 형식 유지
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(validation.getMessage()));
        }

        String email = jwtTokenProvider.getSubject(request.getRefreshToken());

        Map<String, Object> claims = Map.of("email", email);
        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);

        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .expiredAt(accessExpireAt)
                .build();

        // 새 accessToken을 성공적으로 발급했을 때 응답
        return ResponseEntity.ok(ApiResponse.success("엑세스 토큰 갱신 성공", tokenDto));
    }
}
