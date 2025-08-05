package com.example.mainserver.collector.infrastructure;

import com.example.mainserver.collector.domain.GpsLogEvent;
import com.example.mainserver.collector.infrastructure.rabbitmq.GpsLogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class GpsLogEventListener {

    private final GpsLogProducer gpsLogProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGpsLogEvent(GpsLogEvent event) {

        var gpsLogDto = event.getGpsLogDto();

        try{
            gpsLogProducer.sendLogs(gpsLogDto);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
