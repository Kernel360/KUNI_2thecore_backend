package com.example._thecore_back.rest.car.service;


import com.example._thecore_back.rest.car.model.dto.CarDetailDto;
import com.example._thecore_back.rest.car.model.dto.CarSearchDto;
import com.example._thecore_back.rest.car.model.dto.CarSummaryDto;
import com.example._thecore_back.rest.car.db.*;
import com.example._thecore_back.rest.car.exception.CarNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.example._thecore_back.rest.car.db.CarEntity;
import com.example._thecore_back.rest.car.db.CarRepository;
import com.example._thecore_back.rest.car.db.CarStatus;
import com.example._thecore_back.rest.car.model.CarRequest;




@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public CarDetailDto getCar(String carNumber){
        var entity =  carRepository.findByCarNumber(carNumber).orElseThrow(() -> new CarNotFoundException(carNumber));
        return CarDetailDto.EntityToDto(entity);
    }

    public List<CarSearchDto> getAllCars(){

        return carRepository.findAll().stream()
                .map(CarSearchDto::EntityToDto).collect(Collectors.toList());

    }

    public CarSummaryDto getCountByStatus(){

        Map<CarStatus, Long> result = carRepository.getCountByStatus().stream().collect(Collectors.toMap(
                row -> (CarStatus) row[0],
                row -> (Long) row[1]
        ));

        return CarSummaryDto.builder()
                .operating(result.getOrDefault(CarStatus.IN_USE, 0L))
                .waiting(result.getOrDefault(CarStatus.IDLE, 0L))
                .inspecting(result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .total(result.getOrDefault(CarStatus.IN_USE, 0L) +
                        result.getOrDefault(CarStatus.IDLE, 0L)
                + result.getOrDefault(CarStatus.MAINTENANCE, 0L))
                .build();
    }

 
    public boolean validateVerificationCode(String verificationCode) {
        //Todo
        return true;
    }

    public boolean validateConfirmPassword(String confirmPassword) {
        // Todo
        return true;
    }

    public CarEntity createCar( // 차량 등록
            CarRequest carRequest
    ) {
        var entity = CarEntity.builder()
                .brand(carRequest.getBrand())
                .model(carRequest.getModel())
                .year(carRequest.getCarYear())
//                .status(CarStatus.IDLE) // default값 : 대기 중
                .status(carRequest.getStatus())
                .carType(carRequest.getCarType())
                .carNumber(carRequest.getCarNumber())
                .sumDist(carRequest.getSumDist())
                .emulatorId(carRequest.getEmulatorId())
                .build();

        return carRepository.save(entity);
    }


    public CarEntity updateCar( // 차량 정보 업데이트
            CarRequest carRequest,
            String carNumber
    ) {
        CarEntity entity = carRepository.findByCarNumber(carNumber)
                    .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다."));

        if (carRequest.getBrand() != null && !carRequest.getBrand().isBlank()) {
            entity.setBrand(carRequest.getBrand());
        }

        if(carRequest.getModel() != null && !carRequest.getModel().isBlank()) {
            entity.setModel(carRequest.getModel());
        }

        if(carRequest.getYear() != null && !carRequest.getYear().isBlank()) {
            entity.setYear(carRequest.getYear());
        }

        if(carRequest.getStatus() != null && !carRequest.getStatus().isBlank()) {
            var status = CarStatus.fromDisplayName(carRequest.getStatus());
//            entity.setStatus(status);
            entity.setStatus(carRequest.getStatus());
        }

        if(carRequest.getCarType() != null && !carRequest.getCarType().isBlank()) {
            entity.setCarType(carRequest.getCarType());
        }

        if(carRequest.getCarNumber() != null && !carRequest.getCarNumber().isBlank()) {
            entity.setCarNumber(carRequest.getCarNumber());
        }

        if (carRequest.getSumDist() != null) {
            entity.setSumDist(carRequest.getSumDist());
        }

        if (carRequest.getEmulatorId() != null) {
            entity.setEmulatorId(carRequest.getEmulatorId());
        }

        return carRepository.save(entity);
    }


    public Map<String, String> deleteCar( // 차량 삭제
            String carNumber
    ){
        CarEntity entity = carRepository.findByCarNumber(carNumber)
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다."));

        String brand = entity.getBrand();
        String model = entity.getModel();

        carRepository.delete(entity);

        Map<String, String> result = new HashMap<>();

        result.put("brand", model);
        result.put("model", brand);
        result.put("carNumber", carNumber);

        return result;
    }
}
