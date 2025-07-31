package hub.domain;

import hub.domain.dto.GpsLogDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import hub.exception.WebSocketSendException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class GpsWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    //Websocket 연결 시 loginId를 websocket 세션에 추가
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String loginId = (String) session.getAttributes().get("loginId");
        if (loginId != null) {
            sessions.put(loginId, session);
            log.info("Session connected for loginId: {}", loginId);
        }
    }

    //loginId에 맞는 websocket client로 gps log 전송
    public void sendGpsLogToClient(GpsLogDto gpsLogDto) {
        String loginId = gpsLogDto.getLoginId();
        WebSocketSession session = sessions.get(loginId);

        if (session != null && session.isOpen()) {
            try {
                //직렬화 에러
                String jsonMessage = objectMapper.writeValueAsString(gpsLogDto);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("Sent GPS log to loginId: {}", loginId);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize GpsLogDto for loginId: {}", loginId, e);
                //전송 에러
            } catch (IOException e) {
                throw new WebSocketSendException("Failed to send message to loginId: " + loginId, e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String loginId = (String) session.getAttributes().get("loginId");
        if (loginId != null) {
            sessions.remove(loginId);
            log.info("Session closed for loginId: {}", loginId);
        }
    }
}
