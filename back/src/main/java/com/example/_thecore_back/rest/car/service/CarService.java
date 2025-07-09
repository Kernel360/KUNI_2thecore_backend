package com.example._thecore_back.rest.car.service;

import com.example._thecore_back.rest.car.db.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public CarDetailDto getCar(String carNumber){
        var entity =  carRepository.findByCarNumber(carNumber).orElseThrow(() -> new EntityNotFoundException("Car not found"));
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

}
