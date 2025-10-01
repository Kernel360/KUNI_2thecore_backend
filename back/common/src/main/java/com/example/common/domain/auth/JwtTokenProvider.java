package com.example.common.domain.auth;

import com.example.common.infrastructure.JwtProperties;
import com.example.common.exception.InvalidTokenException;
import com.example.common.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(String loginId, Map<String, Object> claims, LocalDateTime expireAt) {
        Date expiration = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(loginId)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    public boolean validateTokenSignature(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    public String getLoginIdFromToken(String token) {
        return getClaims(token).get("loginId", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String createAccessToken(String loginId) {
        Map<String,Object> claims = Map.of("loginId", loginId, "token_type", "access");
        return generateToken(loginId, claims, LocalDateTime.now().plusMinutes(10));
    }

    public long getAccessTokenValidity() {
        return 10 * 60 * 1000L; // 10분
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Authentication getAuthentication(String accessToken) {
        // 기존에 만드신 getClaims 메서드를 호출합니다.
        Claims claims = getClaims(accessToken);

        // 토큰에 별도의 권한 정보가 없으므로, 기본 권한을 부여합니다.
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // UserDetails 객체를 생성합니다.
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // Authentication 객체를 생성하여 반환합니다.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

}
