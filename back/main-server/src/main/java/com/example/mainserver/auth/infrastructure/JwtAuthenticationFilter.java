package com.example.mainserver.auth.infrastructure;

import com.example.mainserver.auth.application.TokenService;
import com.example.common.exception.InvalidTokenException;
import com.example.common.exception.TokenExpiredException;
import com.example.common.domain.auth.JwtTokenProvider;
import com.example.mainserver.auth.domain.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 수정: 모든 OPTIONS 요청은 이 필터 우회 (CORS 프리플라이트를 허용)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/webjars")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/admin/signup")
                || path.startsWith("/actuator/prometheus")
                || path.startsWith("/api/logs/gps")
                || path.startsWith("/api/logs/gps-direct")
                || path.startsWith("/api/drivelogs/start")
                || path.startsWith("/api/drivelogs/end")
                || path.startsWith("/api/drivelogs/update-location")
                || path.startsWith("/api/logs")
                || path.equals("/api/drivelogs/excel")
                || path.startsWith("/ws/**");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Request URI: {}", request.getRequestURI());
        log.debug("Authorization Header: {}", request.getHeader("Authorization"));
        log.debug("Cookies: {}", request.getCookies() != null ? Arrays.toString(request.getCookies()) : "none");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            log.warn("401 Unauthorized: Authorization 헤더 없음");
            sendErrorResponse(response, "Authorization 헤더가 없습니다.", "NO_AUTH_HEADER");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("401 Unauthorized: Bearer 형식 아님");
            sendErrorResponse(response, "Authorization 헤더 형식이 잘못되었습니다.", "INVALID_BEARER_FORMAT");
            return;
        }

        String accessToken = authHeader.substring(7);

        try {
            jwtTokenProvider.validateToken(accessToken);
            log.debug("Access token 유효함");

            if (tokenService.isAccessTokenBlacklisted(accessToken)) {
                log.warn("401 Unauthorized: 블랙리스트 토큰");
                sendErrorResponse(response, "이미 로그아웃된 토큰입니다.", "BLACKLISTED_TOKEN");
                return;
            }

            String loginId = jwtTokenProvider.getLoginIdFromToken(accessToken);
            log.debug("Access token에서 loginId 추출: {}", loginId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginId, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (TokenExpiredException e) {
            log.warn("Access token 만료됨");
            try {
                String refreshToken = extractRefreshTokenFromRequest(request);

                if (refreshToken == null) {
                    log.warn("401 Unauthorized: 쿠키에 refreshToken 없음");
                    sendErrorResponse(response, "리프레시 토큰이 존재하지 않습니다.", "NO_REFRESH_TOKEN");
                    return;
                }

                if (!jwtTokenProvider.validateToken(refreshToken)) {
                    log.warn("401 Unauthorized: refreshToken 유효하지 않음");
                    sendErrorResponse(response, "리프레시 토큰이 유효하지 않습니다.", "INVALID_REFRESH_TOKEN");
                    return;
                }

                TokenDto newTokens = tokenService.reissueAccessToken(refreshToken);
                log.debug("새 액세스 토큰 발급 성공");

                String loginId = jwtTokenProvider.getLoginIdFromToken(newTokens.getAccessToken());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginId, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                response.setHeader("new-access-token", newTokens.getAccessToken());
                response.setHeader("Access-Control-Expose-Headers", "new-access-token");

                filterChain.doFilter(request, response);

            } catch (Exception ex) {
                log.error("토큰 재발급 실패: {}", ex.getMessage());
                sendErrorResponse(response, "토큰 재발급 실패: " + ex.getMessage(), "REISSUE_FAILED");
            }
        } catch (InvalidTokenException e) {
            log.warn("401 Unauthorized: Invalid token - {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), "INVALID_TOKEN");
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생", e);
            sendErrorResponse(response, "서버 오류: " + e.getMessage(), "SERVER_ERROR");
        }
    }

    private String extractRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, String message, String reasonCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", message);
        body.put("reasonCode", reasonCode);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
