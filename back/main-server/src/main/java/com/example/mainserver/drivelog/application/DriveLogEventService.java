package com.example.mainserver.drivelog.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.domain.car.CarReader;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.domain.DriveLogRepository;
import com.example.mainserver.drivelog.dto.DriveLogEventDto;
import com.example.mainserver.drivelog.dto.EndDriveRequestDto;
import com.example.mainserver.drivelog.dto.StartDriveRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriveLogEventService {

    private final DriveLogService driveLogService;

    private final CarReader carReader;

    private final DriveLogRepository driveLogRepository;

    @RabbitListener(queues = "drive.log.queue")
    @Transactional
    public void handleDriveLogEvent(DriveLogEventDto driveLogEventDto){

        log.info("Rabbitmq로 운행 기록 수신 : {}", driveLogEventDto);

        CarEntity car = carReader.findByCarNumber(driveLogEventDto.getCarNumber()).orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND, driveLogEventDto.getCarNumber()));

        var carId = car.getId();

        var currentLatitude = car.getLastLatitude();
        var currentLongitude = car.getLastLongitude();

        if("ON".equals(driveLogEventDto.getStatus())){
            StartDriveRequestDto startDriveRequestDto = StartDriveRequestDto.builder()
                    .carNumber(driveLogEventDto.getCarNumber())
                    .startTime(driveLogEventDto.getEventTime())
                    .startLatitude(currentLatitude)
                    .startLongitude(currentLongitude).build();

            driveLogService.startDrive(startDriveRequestDto);
        }
        else if("OFF".equals(driveLogEventDto.getStatus())){

            Optional<DriveLog> driveLog = driveLogRepository.findByCarIdAndEndTimeIsNull(carId);

            if(driveLog.isPresent()){
                var currentLog = driveLog.get();

                EndDriveRequestDto endDriveRequestDto = EndDriveRequestDto.builder()
                        .carNumber(driveLogEventDto.getCarNumber())
                        .startTime(currentLog.getStartTime())
                        .endTime(LocalDateTime.now())
                        .endLatitude(car.getLastLatitude())
                        .endLongitude(car.getLastLongitude()).build();

                driveLogService.endDrive(endDriveRequestDto);
            }
            else{
                log.error("{}의 종료 주행 기록을 찾을 수 없습니다", car.getCarNumber());
            }
        }

    }


}
