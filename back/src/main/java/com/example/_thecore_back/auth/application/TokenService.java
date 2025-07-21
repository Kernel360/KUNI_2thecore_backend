package com.example._thecore_back.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Refresh 토큰 저장(userEmail키, 토큰 값, 만료시간)
    public void storeRefreshToken(String userEmail, String refreshToken, long expirationMs){
        redisTemplate.opsForValue().set("refresh:" + userEmail, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    // 저장된 Refresh 토큰과 비교 및 검증
    public boolean validateRefreshToken(String userEmail, String refreshToken){
        String savedToken = (String) redisTemplate.opsForValue().get("refresh:" + userEmail);
        return refreshToken.equals(savedToken);
    }

    // 로그아웃 시 Access 토큰 블랙리스트 등록
    public void blacklistAccessToken(String accessToken, long expirationMs){
        redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expirationMs, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트 여부 체크
    public boolean isAccessTokenBlacklisted(String accessToken){
        return redisTemplate.hasKey("blacklist:" + accessToken);
    }

    // 1계정 1세션 유지 -> 기존 토큰 보유 시 블랙리스트 처리 후 저장
    public void enforceSingleSession(String userEmail, String accessToken, long expirationMs){
        String previousToken = (String) redisTemplate.opsForValue().get("access:" + userEmail);
        if (previousToken == null){
            blacklistAccessToken(userEmail, expirationMs);
        }
        redisTemplate.opsForValue().set("access:" + userEmail, accessToken, expirationMs, TimeUnit.MILLISECONDS);
    }
}
