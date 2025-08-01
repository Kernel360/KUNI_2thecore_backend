package com.example._thecore_back.auth.application;

import com.example._thecore_back.auth.domain.JwtTokenProvider;
import com.example._thecore_back.auth.domain.LoginRequest;
import com.example._thecore_back.auth.domain.RefreshRequest;
import com.example._thecore_back.auth.domain.TokenDto;
import com.example._thecore_back.auth.exception.InvalidTokenException;
import com.example._thecore_back.auth.exception.LoginFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public TokenDto login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            String email = authentication.getName();
            Map<String, Object> claims = Map.of("email", email);
            LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);
            LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(7);

            String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
            String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

            tokenService.storeRefreshToken(email, refreshToken, 7 * 24 * 60 * 60 * 1000L);
            tokenService.enforceSingleSession(email, accessToken, 30 * 60 * 1000L);

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
        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);
        LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(7);

        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
        String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

        tokenService.storeRefreshToken(email, refreshToken, 7 * 24 * 60 * 60 * 1000L);
        tokenService.enforceSingleSession(email, accessToken, 30 * 60 * 1000L);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(accessExpireAt)
                .build();
    }
}