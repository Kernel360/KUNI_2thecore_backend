package com.example.mainserver.collector.infrastructure;

import com.example.mainserver.collector.domain.GpsLogEntity;
import com.example.mainserver.collector.domain.GpsLogWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GpsLogWriterImpl implements GpsLogWriter {

    private final GpsLogRepository gpsLogRepository;

    @Override
    public void saveAll(List<GpsLogEntity> logs) {
        gpsLogRepository.saveAll(logs);
    }
}
