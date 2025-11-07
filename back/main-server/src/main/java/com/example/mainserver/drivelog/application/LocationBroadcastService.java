package com.example.mainserver.drivelog.application;

import com.example.common.dto.LiveLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationBroadcastService {
    private final SimpMessagingTemplate messagingTemplate;

    private static final String LIVE_LOCATION_QUEUE = "live.location.queue.main-server";

    // rabbitMQ의 지정된 메세지 큐를 구독
    // 메세지가 큐에 도착 시, 해당 메소드 자동 호출
    @RabbitListener(queues = LIVE_LOCATION_QUEUE)
    public void broadcastLocation(LiveLocationDto locationDto) {
        // 웹소켓 동적 주제 생성
        String destination = "/location/" + locationDto.getCarNumber();

        // SimpMessageingTemplate 이용해 해당 주제 구독하는 클라이언트에게 메세지 전송
        messagingTemplate.convertAndSend(destination, locationDto);
        log.info("{}로 전송 완료 - 차량 번호: {}, 위도: {}, 경도: {}", locationDto.getCarNumber(), locationDto.getCarNumber(), locationDto.getLatitude(), locationDto.getLongitude());
    }

}
