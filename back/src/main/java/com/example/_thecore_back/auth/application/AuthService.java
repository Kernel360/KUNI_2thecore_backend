package com.example._thecore_back.auth.application;

import com.example._thecore_back.auth.domain.JwtTokenProvider;
import com.example._thecore_back.auth.domain.LoginRequest;
import com.example._thecore_back.auth.domain.RefreshRequest;
import com.example._thecore_back.auth.domain.TokenDto;
import com.example._thecore_back.auth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenDto login(LoginRequest request) {
        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 인증된 사용자 이메일
        String email = authentication.getName();

        // claims 및 만료 시간 설정
        Map<String, Object> claims = Map.of("email", email);
        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);
        LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(7);

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
        String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(accessExpireAt)
                .build();
    }

    public TokenDto refresh(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())){
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        // 이메일 추출
        String email = jwtTokenProvider.getSubject(request.getRefreshToken());

        Map<String, Object> claims = Map.of("email", email);

        LocalDateTime accessExpireAt = LocalDateTime.now().plusMinutes(30);
        LocalDateTime refreshExpireAt = LocalDateTime.now().plusDays(7); // 새 리프레시 토큰 만료시간

        String accessToken = jwtTokenProvider.generateToken(email, claims, accessExpireAt);
        String refreshToken = jwtTokenProvider.generateToken(email, claims, refreshExpireAt);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredAt(accessExpireAt)
                .build();
    }
}
