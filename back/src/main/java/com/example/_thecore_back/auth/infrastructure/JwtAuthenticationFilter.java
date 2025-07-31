package com.example._thecore_back.auth.infrastructure;

import com.example._thecore_back.auth.application.TokenService;
import com.example._thecore_back.auth.exception.InvalidTokenException;
import com.example._thecore_back.auth.exception.TokenExpiredException;
import com.example._thecore_back.common.dto.ApiResponse;
import com.example._thecore_back.auth.domain.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Profile("!test") // ⬅ 테스트 프로필에서는 빈 등록 안 함!
// 헤딩 클래스는 헤더 추출, JWT 유효성 검사, 사용자 식별자 추출, 인증 객체 생성, Spring Security 인증 등록 기능
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/webjars")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/admin/signup");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 요청 헤더에서 JWT 추출
        String authToken = request.getHeader("Authorization");

        // 헤더가 없거나 Bearer 형식이 아닌 경우
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            sendErrorResponse(response, "Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
            return;
        }

        // "Bearer " 제거 후 토큰만 추출
        String token = authToken.substring(7);

        // 토큰 검증
        try {
            jwtTokenProvider.validateToken(token); // 유효하지 않으면 예외 발생

            // Redis 블랙리스트 체크
            if (tokenService.isAccessTokenBlacklisted(token)){
                sendErrorResponse(response, "이미 로그아웃된 블랙리스트 토큰입니다");
                return;
            }
            // 인증 객체 생성 및 등록
            String userId = jwtTokenProvider.getSubject(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId, // userId가 principal
                            null,   // 비밀번호는 아직 미검증
                            null    // 권한 처리 필요시 여기에 리스트 전달
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 인증 객체를 SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidTokenException | TokenExpiredException e){
            sendErrorResponse(response, e.getMessage());
            return;
        }
        // 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    // ApiResponse.fail() 기반 JSON 에러 응답 전송 메서드
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.fail(message);
        String json = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(json);
    }
}

