package com.example._thecore_back.car.application;


import com.example._thecore_back.car.controller.dto.*;
import com.example._thecore_back.car.domain.CarReader;
import com.example._thecore_back.car.domain.CarWriter;
import com.example._thecore_back.car.exception.CarAlreadyExistsException;
import com.example._thecore_back.car.exception.CarErrorCode;
//import com.example._thecore_back.car.exception.CarNotFoundByFilterException;
import com.example._thecore_back.car.exception.CarNotFoundException;
import com.example._thecore_back.car.infrastructure.mapper.CarMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example._thecore_back.car.domain.CarEntity;
import com.example._thecore_back.car.domain.CarStatus;


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
                .operating(result.getOrDefault(CarStatus.IN_USE, 0L))
                .waiting(result.getOrDefault(CarStatus.IDLE, 0L))
                .inspecting(result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .total(result.getOrDefault(CarStatus.IN_USE, 0L) +
                        result.getOrDefault(CarStatus.IDLE, 0L)
                        + result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .build();
    }

//    public List<CarSearchDto> getCarsByFilter(String carNumber, String model,
//                                              String brand,    CarStatus status) {
//
//        var result = carMapper.search(carNumber, model, brand, status);
//
//        return result.stream()
//                .map(CarSearchDto::EntityToDto)
//                .toList();
//    }

    public Page<CarSearchDto> getCarsByFilter(CarFilterRequestDto carFilterRequestDto, int page, int size) {

        int offset = (page - 1) * size;

        var result = carMapper.search(carFilterRequestDto, offset, size);

        var total = carMapper.countByFilter(carFilterRequestDto);

        var resultToDto =  result.stream()
                .map(CarSearchDto::EntityToDto)
                .toList();

        return new PageImpl<>(resultToDto, PageRequest.of(page - 1, size), total);
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

        // 차량 번호가 이미 존재할 경우
        if(carReader.findByCarNumber(carRequest.getCarNumber()).isPresent()){
            throw new CarAlreadyExistsException(carRequest.getCarNumber());
        }

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

    public List<CarSearchDto> getCarsInMaintenanceOrIdle() {
        List<CarStatus> statuses = List.of(CarStatus.MAINTENANCE, CarStatus.IDLE);
        List<CarEntity> cars = carReader.findByStatus(statuses);
        return cars.stream()
                .map(CarSearchDto::EntityToDto)
                .toList();
    }
}