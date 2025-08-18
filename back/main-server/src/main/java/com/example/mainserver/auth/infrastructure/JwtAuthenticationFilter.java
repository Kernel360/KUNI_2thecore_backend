package com.example.mainserver.auth.infrastructure;

import com.example.mainserver.auth.application.TokenService;
import com.example.common.exception.InvalidTokenException;
import com.example.common.exception.TokenExpiredException;
import com.example.common.domain.auth.JwtTokenProvider;
import com.example.mainserver.auth.domain.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.common.dto.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/webjars")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/admin/signup")
                || path.startsWith("/actuator/prometheus");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
            return;
        }

        String accessToken = authHeader.substring(7);

        try {
            jwtTokenProvider.validateToken(accessToken);

            String loginId = jwtTokenProvider.getLoginIdFromToken(accessToken);

            if (tokenService.isAccessTokenBlacklisted(accessToken)) {
                sendErrorResponse(response, "이미 로그아웃된 토큰입니다.");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginId, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (TokenExpiredException e) {
            try {
                String refreshToken = tokenService.extractRefreshTokenFromRequest(request);
                if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
                    sendErrorResponse(response, "리프레시 토큰이 유효하지 않습니다.");
                    return;
                }

                TokenDto newTokens = tokenService.reissueAccessToken(refreshToken);
                sendSuccessResponse(response, "새로운 액세스 토큰 발급", newTokens);

            } catch (Exception ex) {
                sendErrorResponse(response, "토큰 재발급 실패: " + ex.getMessage());
            }
        } catch (InvalidTokenException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(message)));
    }

    private void sendSuccessResponse(HttpServletResponse response, String message, TokenDto tokenDto) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(message, tokenDto)));
    }
}