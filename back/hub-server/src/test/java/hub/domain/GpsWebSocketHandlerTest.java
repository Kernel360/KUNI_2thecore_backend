package hub.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import hub.domain.dto.GpsLogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpsWebSocketHandlerTest {

    @Mock
    private WebSocketSession session;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GpsWebSocketHandler gpsWebSocketHandler;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new ConcurrentHashMap<>();
        lenient().when(session.getAttributes()).thenReturn(attributes);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("웹소켓 연결 성공")
    void afterConnectionEstablished_Success() {
        String loginId = "user123";
        attributes.put("loginId", loginId);

        gpsWebSocketHandler.afterConnectionEstablished(session);

        Map<String, WebSocketSession> sessions = (Map<String, WebSocketSession>) ReflectionTestUtils.getField(gpsWebSocketHandler, "sessions");
        assert sessions == null || sessions.containsKey(loginId);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("웹소켓 연결 종료")
    void afterConnectionClosed_Success() {
        String loginId = "user123";
        attributes.put("loginId", loginId);
        gpsWebSocketHandler.afterConnectionEstablished(session);

        gpsWebSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        Map<String, WebSocketSession> sessions = (Map<String, WebSocketSession>) ReflectionTestUtils.getField(gpsWebSocketHandler, "sessions");
        assert sessions == null || !sessions.containsKey(loginId);
    }

    @Test
    @DisplayName("GPS 로그 전송 성공")
    void sendGpsLogToClient_Success() throws IOException {
        String loginId = "user123";
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto(loginId, "1234", Collections.singletonList(gps));
        String jsonMessage = "{\"loginId\":\"user123\",\"carNumber\":\"1234\",\"logList\":[{\"latitude\":\"37.5665\",\"longitude\":\"126.9780\",\"timestamp\":\"" + gps.getTimestamp() + "\"}]}";

        attributes.put("loginId", loginId);
        when(session.isOpen()).thenReturn(true);
        when(objectMapper.writeValueAsString(any(GpsLogDto.class))).thenReturn(jsonMessage);

        gpsWebSocketHandler.afterConnectionEstablished(session);

        gpsWebSocketHandler.sendGpsLogToClient(gpsLogDto);

        verify(session, times(1)).sendMessage(new TextMessage(jsonMessage));
    }

    @Test
    @DisplayName("GPS 로그 전송 실패 - 세션 없음")
    void sendGpsLogToClient_SessionNotFound() throws IOException {
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto("nonexistentUser", "1234", Collections.singletonList(gps));

        gpsWebSocketHandler.sendGpsLogToClient(gpsLogDto);

        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    @DisplayName("GPS 로그 전송 실패 - 세션 닫힘")
    void sendGpsLogToClient_SessionClosed() throws IOException {
        String loginId = "user123";
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto(loginId, "1234", Collections.singletonList(gps));

        attributes.put("loginId", loginId);
        when(session.isOpen()).thenReturn(false);
        gpsWebSocketHandler.afterConnectionEstablished(session);

        gpsWebSocketHandler.sendGpsLogToClient(gpsLogDto);

        verify(session, never()).sendMessage(any(TextMessage.class));
    }
}
