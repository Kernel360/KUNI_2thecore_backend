package com.example.mainserver.auth.application;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.common.dto.ApiResponse;
import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.domain.AutoLoginResponse;
import com.example.mainserver.auth.domain.LoginRequest;
import com.example.mainserver.auth.domain.RefreshRequest;
import com.example.mainserver.auth.domain.TokenDto;
import com.example.mainserver.auth.exception.LoginFailedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
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

    private static final long ACCESS_TOKEN_EXPIRE_MINUTES = 3L; // 3분으로 변경
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 7L;

    // 로그인
    public TokenDto login(LoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            String loginId = authentication.getName();

            String accessToken = jwtTokenProvider.generateToken(
                    loginId,
                    Map.of("loginId", loginId, "token_type", "access"),
                    LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
            );

            String refreshToken = jwtTokenProvider.generateToken(

                    loginId,
                    Map.of("loginId", loginId, "token_type", "refresh"),
                    LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS)
            );

            // Redis 저장
            tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
            tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

            // 쿠키 전송
            sendRefreshTokenCookie(response, refreshToken);

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(null)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    // 토큰 갱신
    public TokenDto refresh(HttpServletResponse response, RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getSubject(request.getRefreshToken());

        if (!tokenService.validateRefreshToken(loginId, request.getRefreshToken())) {
            throw new InvalidTokenException("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateToken(
                loginId,
                Map.of("loginId", loginId, "token_type", "access"),
                LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
        );

        String refreshToken = jwtTokenProvider.generateToken(
                loginId,
                Map.of("loginId", loginId, "token_type", "refresh"),
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS)
        );

        // Redis 갱신
        tokenService.storeRefreshToken(loginId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
        tokenService.enforceSingleSession(loginId, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        sendRefreshTokenCookie(response, refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(null)
                .build();
    }

    // 자동 로그인
    public ApiResponse<AutoLoginResponse> autoLogin(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractAccessTokenFromHeader(request);

        if (accessToken != null && jwtTokenProvider.validateTokenSignature(accessToken) && !jwtTokenProvider.isTokenExpired(accessToken)) {
            return ApiResponse.success("엑세스 토큰 유효", new AutoLoginResponse(accessToken));
        }

        String refreshToken = extractRefreshTokenFromCookie(request, "refreshToken");
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ApiResponse.fail("리프레시 토큰이 유효하지 않습니다.");
        }

        String loginId = jwtTokenProvider.getSubject(refreshToken);
        if (!tokenService.validateRefreshToken(loginId, refreshToken)) {
            return ApiResponse.fail("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.generateToken(
                loginId,
                Map.of("loginId", loginId, "token_type", "access"),
                LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES)
        );

        tokenService.enforceSingleSession(loginId, newAccessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        return ApiResponse.success("엑세스 토큰 재발급 성공", new AutoLoginResponse(newAccessToken));
    }

    // Access Token 추출
    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return null;
    }

    // Refresh Token 쿠키 추출
    private String extractRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) return cookie.getValue();
        }
        return null;
    }


    private void sendRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // Cross-origin 환경을 위한 쿠키 설정 (SameSite 제거)
        String cookieValue = String.format(
            "refreshToken=%s; Path=/; Max-Age=%d; HttpOnly",
            refreshToken,
            Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toSeconds()
        );
        
        response.addHeader("Set-Cookie", cookieValue);
    }
}
