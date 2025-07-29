package com.example._thecore_back.auth.domain;

import com.example._thecore_back.auth.exception.InvalidTokenException;
import com.example._thecore_back.auth.exception.TokenExpiredException;
import com.example._thecore_back.auth.infrastructure.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(String subject, Map<String, Object> claims, LocalDateTime expireAt) {
        Date expiration = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e){
            log.warn("Expired token: {}", e.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException e){
            log.warn("Invalid token: {}", e.getMessage());
            throw new InvalidTokenException("유효하지 앟은 토큰입니다.");
        }
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}