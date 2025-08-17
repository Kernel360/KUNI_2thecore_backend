package com.example.mainserver.auth.application;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.common.dto.ApiResponse;
import com.example.mainserver.auth.domain.*;
import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.exception.LoginFailedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public TokenDto login(LoginRequest request, HttpServletResponse response) {
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

            // Redis 저장
            tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
            tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

            // HttpOnly 쿠키로 Refresh Token 전송 (개발환경용 설정)
            String cookieValue = String.format(
                    "refreshToken=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax",
                    refreshToken,
                    Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).getSeconds()
            );
            response.addHeader("Set-Cookie", cookieValue);

            log.info("RefreshToken 쿠키 설정 완료: loginId={}", loginId);

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(null) // 클라이언트 JS에서 못 보게 null 반환
                    .build();

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    // 리프레시 토큰으로 토큰 갱신
    public TokenDto refresh(HttpServletResponse response, RefreshRequest request) {
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

        // Redis 갱신
        tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
        tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        // 쿠키 갱신 (개발환경용 설정)
        String cookieValue = String.format(
                "refreshToken=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax",
                refreshToken,
                Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).getSeconds()
        );
        response.addHeader("Set-Cookie", cookieValue);

        log.info("RefreshToken 쿠키 갱신 완료: loginId={}", loginId);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(null)
                .build();
    }

    // 자동 로그인
    public ApiResponse<AutoLoginResponse> autoLogin(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = extractAccessTokenFromHeader(request);

        if (accessToken == null) {
            return ApiResponse.fail("Access Token이 없습니다.");
        }

        if (!jwtTokenProvider.validateTokenSignature(accessToken)) {
            return ApiResponse.fail("등록된 적 없는 access token 입니다.");
        }

        // 만료되지 않은 경우
        if (!jwtTokenProvider.isTokenExpired(accessToken)) {
            return ApiResponse.success("엑세스 토큰 유효", new AutoLoginResponse(null));
        }

        // HttpOnly 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshTokenFromCookie(request, "refreshToken");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ApiResponse.fail("리프레시 토큰이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getSubject(refreshToken);

        if (!tokenService.validateRefreshToken(loginId, refreshToken)) {
            return ApiResponse.fail("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        // 만료된 경우 새 Access Token 발급
        Map<String, Object> claims = Map.of("loginId", loginId, "token_type", "access");
        String newAccessToken = jwtTokenProvider.generateToken(
                loginId, claims, LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
        );

        tokenService.enforceSingleSession(loginId, newAccessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());
        return ApiResponse.success("엑세스 토큰 재발급 성공", new AutoLoginResponse(newAccessToken));
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