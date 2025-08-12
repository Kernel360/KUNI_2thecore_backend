package hub.controller;


import hub.application.ConsumerService;
import hub.domain.dto.GpsLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hub")
@RequiredArgsConstructor
@Slf4j
public class HubController {

    private final ConsumerService consumerService;

    @PostMapping("/gps-direct")
    public void gpsDirect(@RequestBody GpsLogDto gpsLogDto) {

        log.info("GpsLogDto: {}", gpsLogDto);
        consumerService.gpsConsumerDirect(gpsLogDto);
    }


}
