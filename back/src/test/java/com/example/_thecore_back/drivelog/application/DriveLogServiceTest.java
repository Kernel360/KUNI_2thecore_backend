package com.example._thecore_back.drivelog.application;

import com.example._thecore_back.drivelog.domain.DriveLog;
import com.example._thecore_back.drivelog.domain.DriveLogRepository;
import com.example._thecore_back.drivelog.dto.DriveLogRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class DriveLogServiceTest {

    @Autowired
    private DriveLogService driveLogService;

    @Autowired
    private DriveLogRepository driveLogRepository;

    @Test
    public void testCreateDriveLogAndPrint() {
        DriveLogRequest request = DriveLogRequest.builder()
                .carId(1L)
                .startPoint("서울역")
                .startLatitude("37.555")
                .startLongitude("126.972")
                .startTime(LocalDateTime.now().minusMinutes(30))
                .endPoint("강남역")
                .endLatitude("37.497")
                .endLongitude("127.028")
                .endTime(LocalDateTime.now())
                .driveDist(new BigDecimal("12.34"))
                .speed("45km/h")
                .build();

        DriveLog saved = driveLogService.save(request);

        System.out.println("저장된 DriveLog ID: " + saved.getDriveLogId());
        System.out.println("저장된 StartPoint: " + saved.getStartPoint());

        List<DriveLog> logs = driveLogRepository.findAll();
        assertFalse(logs.isEmpty(), "DB에 저장된 로그가 있어야 합니다.");

    }

}
