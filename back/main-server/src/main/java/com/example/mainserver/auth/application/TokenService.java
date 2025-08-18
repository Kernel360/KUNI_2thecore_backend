package com.example.mainserver.auth.application;

import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.domain.TokenDto;
import com.example.common.domain.auth.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public void storeRefreshToken(String loginId, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set("refresh:" + loginId, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean validateRefreshToken(String loginId, String refreshToken) {
        String savedToken = (String) redisTemplate.opsForValue().get("refresh:" + loginId);
        return refreshToken.equals(savedToken);
    }

    public void blacklistAccessToken(String accessToken, long expirationMs) {
        redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        return redisTemplate.hasKey("blacklist:" + accessToken);
    }

    public void enforceSingleSession(String loginId, String accessToken, long expirationMs) {
        String previousToken = (String) redisTemplate.opsForValue().get("access:" + loginId);
        if (previousToken != null) {
            blacklistAccessToken(previousToken, expirationMs);
        }
        redisTemplate.opsForValue().set("access:" + loginId, accessToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String loginId){
        return (String) redisTemplate.opsForValue().get("refresh:" + loginId);
    }

    public String extractRefreshTokenFromRequest(HttpServletRequest request){
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("refreshToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public TokenDto reissueAccessToken(String refreshToken){
        String loginId = jwtTokenProvider.getLoginIdFromToken(refreshToken);
        String savedToken = getRefreshToken(loginId);

        if (savedToken == null || !savedToken.equals(refreshToken)){
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(loginId);
        enforceSingleSession(loginId, newAccessToken, jwtTokenProvider.getAccessTokenValidity());

        return new TokenDto(newAccessToken, refreshToken);
    }
}
