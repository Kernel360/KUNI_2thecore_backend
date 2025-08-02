package com.example._thecore_back.collector;

import com.example._thecore_back.collector.domain.GpsLogEntity;
import com.example._thecore_back.collector.infrastructure.GpsLogRepository;
import com.example._thecore_back.collector.infrastructure.GpsLogWriterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = {
        "com.example._thecore_back.collector.domain",
        "com.example._thecore_back.car.domain",
        "com.example._thecore_back.admin.domain",
        "com.example._thecore_back.emulatorserver"
})
public class GpsLogWriterImplTest { 

    @Autowired
    private GpsLogRepository gpsLogRepository;

    private GpsLogWriterImpl gpsLogWriter;


    @BeforeEach
    void setUp() {
        gpsLogWriter = new GpsLogWriterImpl(gpsLogRepository); // 수동 주입
    }

    @Test
    void saveAllGpsLogWriterImpl() {
        List<GpsLogEntity> logs = List.of(
                new GpsLogEntity(),
        new GpsLogEntity()
        );

        gpsLogWriter.saveAll(logs);

        List<GpsLogEntity> saved = gpsLogRepository.findAll();
        assertThat(saved).hasSize(2); // 저장된 거 검증
    }

}
