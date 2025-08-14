package com.example.mainserver.auth.application;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.common.dto.ApiResponse;
import com.example.mainserver.auth.domain.*;
import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.exception.LoginFailedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    private static final long ACCESS_TOKEN_EXPIRE_MINUTES = 10L; // 10분
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 7L; // 7일

    // 로그인
    public TokenDto login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            String loginId = authentication.getName();

            Map<String, Object> accessClaims = Map.of(
                    "loginId", loginId,
                    "token_type", "access"
            );

            Map<String, Object> refreshClaims = Map.of(
                    "loginId", loginId,
                    "token_type", "refresh"
            );

            LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES);
            LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS);

            String accessToken = jwtTokenProvider.generateToken(loginId, accessClaims, accessExpireAt);
            String refreshToken = jwtTokenProvider.generateToken(loginId, refreshClaims, refreshExpireAt);

            tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
            tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    // 리프레시 토큰으로 토큰 갱신
    public TokenDto refresh(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getSubject(request.getRefreshToken());

        if (!tokenService.validateRefreshToken(loginId, request.getRefreshToken())) {
            throw new InvalidTokenException("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        Map<String, Object> accessClaims = Map.of(
                "loginId", loginId,
                "token_type", "access"
        );

        Map<String, Object> refreshClaims = Map.of(
                "loginId", loginId,
                "token_type", "refresh"
        );

        String accessToken = jwtTokenProvider.generateToken(
                loginId, accessClaims, LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
        );
        String refreshToken = jwtTokenProvider.generateToken(
                loginId, refreshClaims, LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS)
        );

        tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
        tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 자동 로그인
    public ApiResponse<AutoLoginResponse> autoLogin(HttpServletRequest request) {

        String accessToken = extractAccessTokenFromHeader(request);

        // 0. Access Token 존재 여부 확인
        if (accessToken == null) {
            return ApiResponse.fail("Access Token이 없습니다.");
        }

        // 1. Access Token 서명 체크
        if (!jwtTokenProvider.validateTokenSignature(accessToken)) {
            return ApiResponse.fail("등록된 적 없는 access token 입니다.");
        }

        // 2. Access Token 만료 여부 확인
        if (!jwtTokenProvider.isTokenExpired(accessToken)) {
            // 만료되지 않은 경우 data에는 새 토큰 없이 valid만 true
            return ApiResponse.success("엑세스 토큰 유효", new AutoLoginResponse(true, null));
        }

        // 3. HttpOnly 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshTokenFromCookie(request, "refreshToken");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ApiResponse.fail("리프레시 토큰이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getSubject(refreshToken);

        // 4. Redis에 저장된 Refresh Token과 일치 여부 확인
        if (!tokenService.validateRefreshToken(loginId, refreshToken)) {
            return ApiResponse.fail("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        // 5. 만료된 경우 새 Access Token 발급
        Map<String, Object> claims = Map.of("loginId", loginId, "token_type", "access");
        String newAccessToken = jwtTokenProvider.generateToken(
                loginId, claims, LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
        );

        tokenService.enforceSingleSession(loginId, newAccessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        return ApiResponse.success("엑세스 토큰 재발급 성공", new AutoLoginResponse(true, newAccessToken));
    }

    // Access Token 헤더 추출
    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Refresh Token 쿠키 추출
    private String extractRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
