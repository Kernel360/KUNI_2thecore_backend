package com.example.mainserver;

import com.example.mainserver.auth.application.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenServiceTest {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String userEmail = "user@example.com";
    private final String refreshToken = "refreshToken123";
    private final long expirationMs = 10000L;

    @BeforeEach
    void clearRedis() {
        redisTemplate.delete("refresh:" + userEmail);
        redisTemplate.delete("access:" + userEmail);
        System.out.println("Redis 초기화 완료");

    }

    @Test
    void testStoreAndValidateRefreshToken() {
        tokenService.storeRefreshToken(userEmail, refreshToken, expirationMs);
        System.out.println("Refresh 토큰 저장 완료");

        boolean isValid = tokenService.validateRefreshToken(userEmail, refreshToken);
        System.out.println("검증된 Refresh Token 유효 여부: " + isValid);
        assertTrue(isValid);
    }

    @Test
    void testValidateInvalidRefreshToken() {
        tokenService.storeRefreshToken(userEmail, refreshToken, expirationMs);
        System.out.println("Refresh 토큰 저장 완료");

        boolean isValid = tokenService.validateRefreshToken(userEmail, "wrongToken");
        System.out.println("잘못된 토큰 유효 여부: " + isValid);
        assertFalse(isValid);
    } // 여기까지 Refresh 토큰 저장 및 검증 테스트

    @Test
    void testBlacklistAccessToken() {
        String accessToken = "accessToken456";

        tokenService.blacklistAccessToken(accessToken, expirationMs);
        System.out.println("Access 토큰 블랙리스트 등록 완료");

        boolean isBlacklisted = tokenService.isAccessTokenBlacklisted(accessToken);
        System.out.println("블랙리스트 여부: " + isBlacklisted);
        assertTrue(isBlacklisted);
    } // 여기까지 Access 토큰 블랙리스트 테스트

    @Test
    void testEnforceSingleSession() {
        String newAccessToken = "newAccessToken";
        String oldAccessToken = "oldAccessToken";

        // 이전 토큰 저장
        tokenService.enforceSingleSession(userEmail, oldAccessToken, expirationMs);
        System.out.println("첫 Access 토큰 저장 완료");

        // 새로운 토큰으로 세션 갱신
        tokenService.enforceSingleSession(userEmail, newAccessToken, expirationMs);
        System.out.println("새 Access 토큰으로 덮어쓰기 완료");

        // 현재 저장된 토큰이 새로운 토큰인지 확인
        String storedToken = (String) redisTemplate.opsForValue().get("access:" + userEmail);
        System.out.println("현재 Redis에 저장된 토큰: " + storedToken);
        assertEquals(newAccessToken, storedToken); // 여기까지 1계정 1세션 강제 테스트
    }
}
