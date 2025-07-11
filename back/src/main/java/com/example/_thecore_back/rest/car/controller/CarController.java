package com.example._thecore_back.rest.car.controller;
import com.example._thecore_back.rest.car.model.*;
import com.example._thecore_back.rest.car.model.dto.CarDetailDto;
import com.example._thecore_back.rest.car.model.dto.CarSearchDto;
import com.example._thecore_back.rest.car.model.dto.CarSummaryDto;
import com.example._thecore_back.rest.car.model.CarResponse;
import com.example._thecore_back.rest.car.service.CarService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class CarController {

    private final CarService carService;

//    @GetMapping("")
//    public
    @GetMapping("/{car_number}")
    public ApiResponse<CarDetailDto> getCar(@PathVariable String car_number) {

        var response = carService.getCar(car_number);

//        return CarResponse.<CarDetailDto>builder()
//                .result("OK")
//                .message("find car")
//                .data(response)
//                .build();

        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<CarSearchDto>> getAllCars() {

        var response = carService.getAllCars();

        return ApiResponse.success(response);
    }

    @GetMapping("/statistics")
    public ApiResponse<CarSummaryDto> getCountByStatus() {
        var response = carService.getCountByStatus();

        return ApiResponse.success(response);
    }
    

    // 차량 등록
    @PostMapping("")
    public Api<CarResponse> createCar(
            @RequestBody
            @Validated(CreateGroup.class)
            CarRequest carRequest
    ){
       var savedCar = carService.createCar(carRequest);

        return Api.<CarResponse>builder()
                .result(String.valueOf(HttpStatus.OK.value()))
                .message("차량 등록이 성공적으로 완료되었습니다.")
                .data(CarResponse.from(savedCar))
                .build();
    }

    // 차량 정보 업데이트
    @PatchMapping("/{car_number}")
    public Api<CarResponse> updateCar(
            @PathVariable("car_number")
            String carNumber,
            @RequestBody
            @Validated
            CarRequest carRequest
    ){
        var savedCar = carService.updateCar(carRequest, carNumber);

        return Api.<CarResponse>builder()
                .result(String.valueOf(HttpStatus.OK.value()))
                .message("차량 정보가 성공적으로 수정되었습니다.")
                .data(CarResponse.from(savedCar))
                .build();
    }

    @DeleteMapping("/{car_number}")
    public Api<Map<String, String>> deleteCar(
            @PathVariable("car_number")
            String carNumber
    ){
        Map<String, String> deletedCar = carService.deleteCar(carNumber);

        return Api.<Map<String, String>>builder()
                .result(String.valueOf(HttpStatus.OK.value()))
                .message("차량 삭제가 성공적으로 완료되었습니다.")
                .data(deletedCar)
                .build();
    }
}
