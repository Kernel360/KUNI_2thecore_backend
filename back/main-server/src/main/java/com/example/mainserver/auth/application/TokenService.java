package com.example.mainserver.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void storeRefreshToken(String userEmail, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set("refresh:" + userEmail, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean validateRefreshToken(String userEmail, String refreshToken) {
        String savedToken = (String) redisTemplate.opsForValue().get("refresh:" + userEmail);
        return refreshToken.equals(savedToken);
    }

    public void blacklistAccessToken(String accessToken, long expirationMs) {
        redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        return redisTemplate.hasKey("blacklist:" + accessToken);
    }

    public void enforceSingleSession(String userEmail, String accessToken, long expirationMs) {
        String previousToken = (String) redisTemplate.opsForValue().get("access:" + userEmail);
        if (previousToken != null) {
            blacklistAccessToken(previousToken, expirationMs);
        }
        redisTemplate.opsForValue().set("access:" + userEmail, accessToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    // Redis 서버에서 리프레시 토큰을 가져오는 메서드 추가
    public String getRefreshToken(String userEmail){
        return (String) redisTemplate.opsForValue().get("refresh:" + userEmail);
    }
}