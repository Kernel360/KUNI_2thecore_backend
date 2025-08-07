package com.example.mainserver.auth.application;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.mainserver.auth.domain.LoginRequest;
import com.example.mainserver.auth.domain.RefreshRequest;
import com.example.mainserver.auth.domain.TokenDto;
import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.exception.LoginFailedException;
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

    private static final long ACCESS_TOKEN_EXPIRE_MINUTES = 10000L; // 약 7일
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 21L; // 약 3주

    public TokenDto login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            String email = authentication.getName();
            Map<String, Object> claims = Map.of("email", email);

            LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES);
            LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS);

            String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
            String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

            tokenService.storeRefreshToken(email, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
            tokenService.enforceSingleSession(email, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiredAt(accessExpireAt)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    public TokenDto refresh(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        String email = jwtTokenProvider.getSubject(request.getRefreshToken());

        if (!tokenService.validateRefreshToken(email, request.getRefreshToken())) {
            throw new InvalidTokenException("서버에 저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        Map<String, Object> claims = Map.of("email", email);
        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES);
        LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS);

        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
        String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

        tokenService.storeRefreshToken(email, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRE_DAYS).toMillis());
        tokenService.enforceSingleSession(email, accessToken, Duration.ofMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toMillis());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(accessExpireAt)
                .build();
    }
}
