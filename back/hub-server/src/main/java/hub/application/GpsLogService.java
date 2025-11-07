package hub.application;


import hub.domain.GpsLogEntity;
import hub.domain.GpsLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsLogService {

    private final GpsLogRepository gpsLogRepository;

    @Transactional
    public void saveLog(List<GpsLogEntity> gpsLogEntities) {
        gpsLogRepository.saveAll(gpsLogEntities);
    }

}
