package com.example._thecore_back.rest.car.controller;

import com.example._thecore_back.rest.car.db.CarDetailDto;
import com.example._thecore_back.rest.car.db.CarSearchDto;
import com.example._thecore_back.rest.car.db.CarSummaryDto;
import com.example._thecore_back.rest.car.model.CarResponse;
import com.example._thecore_back.rest.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class CarController {

    private final CarService carService;

//    @GetMapping("")
//    public
    @GetMapping("/{car_number}")
    public CarResponse<CarDetailDto> getCar(@PathVariable String car_number) {

        var response = carService.getCar(car_number);

        return CarResponse.<CarDetailDto>builder()
                .result("OK")
                .message("find car")
                .data(response)
                .build();
    }

    @GetMapping("/search")
    public CarResponse<List<CarSearchDto>> getAllCars() {

        var response = carService.getAllCars();

        return CarResponse.<List<CarSearchDto>>builder()
                .result("OK")
                .message("All Car")
                .data(response)
                .build();
    }

    @GetMapping("/statistics")
    public CarResponse<CarSummaryDto> getCountByStatus() {
        var response = carService.getCountByStatus();

        return CarResponse.<CarSummaryDto>builder()
                .result("OK")
                .message("great")
                .data(response)
                .build();
    }
}
