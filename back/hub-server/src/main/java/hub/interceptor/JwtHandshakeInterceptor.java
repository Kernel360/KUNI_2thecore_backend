package hub.interceptor;

import com.example.common.domain.auth.JwtTokenProvider;
import com.example.common.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    //loginId 추출을 위한 jwtTokenProvider
    private final JwtTokenProvider jwtTokenProvider;

    /*
    1. request에서 loginId 추출
    2. websocket 속성에 추가
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String authorizationHeader = request.getHeaders().getFirst("Authorization");

        //Bearer 접두사 제외
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    //loginId 추출
                    String loginId = jwtTokenProvider.getLoginIdFromToken(token);
                    if (loginId != null) {
                        //세션 별 loginId put
                        attributes.put("loginId", loginId);
                        log.info("WebSocket handshake successful for loginId: {}", loginId);
                        return true;
                    }
                }
            } catch (InvalidTokenException e) {
                log.error("Invalid JWT token for WebSocket handshake: {}", e.getMessage());
                return false;
            }
        }
        log.warn("WebSocket handshake failed. No or invalid Authorization header.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("Exception after WebSocket handshake: {}", exception.getMessage());
        }
    }
}
