package hub.api;

import hub.domain.dto.GpsLogDto;
import hub.application.GpsProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
@Slf4j
public class GpsController {

    private final GpsProducerService gpsProducerService;

    @PostMapping("/publish")
    public String publishGpsData(@RequestBody GpsLogDto gpsLogDto) {
        log.info("📡 Received GPS data from frontend: {}", gpsLogDto);
        gpsProducerService.sendGpsData(gpsLogDto);
        return "Message sent to RabbitMQ successfully.";
    }
}
