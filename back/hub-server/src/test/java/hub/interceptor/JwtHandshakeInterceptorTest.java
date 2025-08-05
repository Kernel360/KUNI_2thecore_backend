package hub.interceptor;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.common.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtHandshakeInterceptorTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    @InjectMocks
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

    private Map<String, Object> attributes;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        headers = new HttpHeaders();
        lenient().when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    @DisplayName("핸드셰이크 성공 테스트")
    void beforeHandshake_Success() throws Exception {

        String token = "MockingToken";
        String loginId = "user123";
        headers.set("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getLoginIdFromToken(token)).thenReturn(loginId);

        //loginId -> 세션 attribute로 put 성공 시 True 반환
        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertTrue(result);
        assertEquals(loginId, attributes.get("loginId"));
    }

    @Test
    @DisplayName("핸드셰이크 실패 - 토큰 없음")
    void beforeHandshake_NoToken() throws Exception {

        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("핸드셰이크 실패 - 토큰 형식 오류")
    void beforeHandshake_InvalidTokenFormat() throws Exception {

        String token = "invalid.token.format";
        headers.set("Authorization", token);

        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("핸드셰이크 실패 - 유효하지 않은 토큰")
    void beforeHandshake_InvalidToken() throws Exception {
        String token = "invalid.token.here";
        headers.set("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenThrow(new InvalidTokenException("Invalid token"));

        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("핸드셰이크 실패 - loginId 추출 실패")
    void beforeHandshake_LoginIdExtractionFails() throws Exception {

        String token = "valid.token.no.loginid";
        headers.set("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getLoginIdFromToken(token)).thenReturn(null);

        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("afterHandshake 예외 로깅 테스트")
    void afterHandshake_withException() {
        Exception exception = new Exception("Test Exception");
        jwtHandshakeInterceptor.afterHandshake(request, response, wsHandler, exception);
    }

    @Test
    @DisplayName("afterHandshake 예외 없음 테스트")
    void afterHandshake_withoutException() {
        jwtHandshakeInterceptor.afterHandshake(request, response, wsHandler, null);
    }
}
