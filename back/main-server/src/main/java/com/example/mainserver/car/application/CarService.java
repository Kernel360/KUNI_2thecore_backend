package com.example.mainserver.car.application;


import com.example.common.domain.car.CarEntity;
import com.example.common.dto.CarRequestDto;
import com.example.mainserver.car.controller.dto.*;
import com.example.common.domain.car.CarReader;
import com.example.mainserver.car.domain.CarWriter;
import com.example.mainserver.car.exception.CarAlreadyExistsException;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import com.example.mainserver.car.infrastructure.mapper.CarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.common.domain.car.CarStatus;


@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarReader carReader;
    private final CarWriter carWriter;
    private final CarMapper carMapper;

    public CarDetailDto getCar(String carNumber){
        var entity =  carReader.findByCarNumber(carNumber).orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));
        return CarDetailDto.EntityToDto(entity);
    }

    public Page<CarDetailDto> getAllCars(Pageable pageable){

        return carReader.findAll(pageable).map(CarDetailDto::EntityToDto);

    }

    public CarSummaryDto getCountByStatus(){

        Map<CarStatus, Long> result = carReader.getCountByStatus();

        return CarSummaryDto.builder()
                .driving(result.getOrDefault(CarStatus.DRIVING, 0L))
                .idle(result.getOrDefault(CarStatus.IDLE, 0L))
                .maintenance(result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .total(result.getOrDefault(CarStatus.DRIVING, 0L) +
                        result.getOrDefault(CarStatus.IDLE, 0L)
                        + result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .build();
    }

    public Page<CarSearchDto> getCarsByFilter(CarFilterRequestDto carFilterRequestDto, int page, int size) {

        int offset = (page - 1) * size;

        var result = carMapper.search(carFilterRequestDto, offset, size);

        var total = carMapper.countByFilter(carFilterRequestDto);

        var resultToDto =  result.stream()
                .map(CarSearchDto::EntityToDto)
                .toList();

        return new PageImpl<>(resultToDto, PageRequest.of(page - 1, size, Sort.by("carNumber").ascending()), total);
    }



    public CarDetailDto createCar( // 차량 등록
                                   CarRequestDto carRequest
    ) {
        boolean isCarNumberExists = carReader.findByCarNumber(carRequest.getCarNumber()).isPresent();

        if (isCarNumberExists) {
            throw new CarAlreadyExistsException(
                    carRequest.getCarNumber()
            );
        }
        var entity = CarEntity.builder()
                .loginId(carRequest.getLoginId())
                .brand(carRequest.getBrand())
                .model(carRequest.getModel())
                .carYear(carRequest.getCarYear())
                .status(carRequest.getStatus() != null && !carRequest.getStatus().isBlank()
                        ? CarStatus.fromDisplayName(carRequest.getStatus())
                        : CarStatus.IDLE) // default값 : 대기 중
                .carType(carRequest.getCarType())
                .carNumber(carRequest.getCarNumber())
                .sumDist(carRequest.getSumDist())
                .build();

        return CarDetailDto.EntityToDto(carWriter.save(entity));
    }


    public CarDetailDto updateCar( // 차량 정보 업데이트
                                   CarRequestDto carRequest,
                                   String carNumber
    ) {
        // 수정하려는 차량이 존재하지 않는 경우
        CarEntity entity = carReader.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

        entity.updateInfo(carRequest); // Entity 내부에서 유효성 검사 후 업데이트

        return CarDetailDto.EntityToDto(carWriter.save(entity));
    }


    public CarDeleteDto deleteCar( // 차량 삭제
                                   String carNumber
    ){
        CarEntity entity = carReader.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER,carNumber));

        carWriter.delete(entity);

        return CarDeleteDto.EntityToDto(entity);
    }

    public void updateLastLocation(String carNumber, String latitude, String longitude) {
        var car = carReader.findByCarNumber(carNumber)
                .orElseThrow(() -> new RuntimeException("차량 없음"));

        car.setLastLatitude(latitude);
        car.setLastLongitude(longitude);

        carWriter.save(car);
    }

    /**
     * 상태 문자열에 따라 차량 리스트 조회
     * statusStr == null -> DRIVING, IDLE, MAINTENANCE 모두 조회
     */
    public List<CarEntity> getCarsByStatusString(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            List<CarStatus> allStatuses = List.of(CarStatus.DRIVING, CarStatus.IDLE, CarStatus.MAINTENANCE);
            return carReader.findByStatus(allStatuses);
        }

        CarStatus status = CarStatus.fromDisplayName(statusStr);
        List<CarEntity> list = carReader.findByStatus(List.of(status));
        log.debug("getCarsByStatusString - querying status {} -> found {}", status, list.size());
        return list;
    }


    /**
     * 위치 정보 반환: getCarsByStatusString 재사용
     */
    public List<CarLocationDto> getCarLocationsByStatus(String statusStr) {
        List<CarEntity> cars = getCarsByStatusString(statusStr);
        return cars.stream()
                .map(CarLocationDto::fromEntity)
                .collect(Collectors.toList());
    }
}
