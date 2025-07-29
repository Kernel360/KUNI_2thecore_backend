package com.example._thecore_back.hub.infrastructure;

import com.example._thecore_back.hub.domain.GpsLogEntity;
import com.example._thecore_back.hub.domain.GpsLogWriter;
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
