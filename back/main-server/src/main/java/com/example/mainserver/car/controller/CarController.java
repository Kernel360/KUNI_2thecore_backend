package com.example.mainserver.car.controller;
import com.example.common.dto.ApiResponse;
import com.example.common.dto.CarRequestDto;
import com.example.mainserver.car.controller.dto.*;
import com.example.common.group.CreateGroup;
import com.example.mainserver.car.application.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
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

    @GetMapping
    public ApiResponse<Page<CarDetailDto>> getAllCars(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size)
    {

        Pageable pageable = PageRequest.of(page - 1, size);

        var response = carService.getAllCars(pageable);

        return ApiResponse.success(response);
    }

    @GetMapping("/statistics")
    public ApiResponse<CarSummaryDto> getCountByStatus() {
        var response = carService.getCountByStatus();

        return ApiResponse.success(response);
    }

    /**
     *
     * @param carFilterRequestDto // 차량 조건 DTO
     * @param page // 프론트에서 요청하는 해당 페이지 넘버 1부터 시작
     * @param offset // size 계산하기 위한 파라미터
     * @return
     */
    @GetMapping("/search")
    public ApiResponse<Page<CarSearchDto>> getCarsByFilter(
            @ModelAttribute CarFilterRequestDto carFilterRequestDto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int offset
    ) {
        log.info("Request DTO: {}", carFilterRequestDto);

        var response = carService.getCarsByFilter(carFilterRequestDto, page, offset);
        return ApiResponse.success(response);
    }

    // 차량 등록
    @PostMapping
    public ApiResponse<CarDetailDto> createCar(
            @RequestBody
            @Validated(CreateGroup.class)
            CarRequestDto carRequest
    ){
        var response = carService.createCar(carRequest);

        return ApiResponse.success("차량 등록이 성공적으로 완료되었습니다.", response);
    }

    // 차량 정보 업데이트
    @PatchMapping("/{car_number}")
    public ApiResponse<CarDetailDto> updateCar(
            @PathVariable("car_number")
            String carNumber,
            @RequestBody
            @Validated
            CarRequestDto carRequest
    ){
        var response = carService.updateCar(carRequest, carNumber);

        return ApiResponse.success("차량 정보가 성공적으로 수정되었습니다.",response);
    }

    // 차량 삭제
    @DeleteMapping("/{car_number}")
    public ApiResponse<CarDeleteDto> deleteCar(
            @PathVariable("car_number")
            String carNumber
    ){
        var response = carService.deleteCar(carNumber);

        return ApiResponse.success("차량 삭제가 성공적으로 완료되었습니다.",response);
    }

    // 점검중 또는 대기중 상태 차량 조회 API
    @GetMapping("/status")
    public ApiResponse<List<CarSearchDto>> getCarsByStatuses(
            @RequestParam(required = false) String status) {
        var cars = carService.getCarsByStatusString(status);

        var resultDtos = cars.stream()
                .map(CarSearchDto::EntityToDto)
                .collect(Collectors.toList());
        return ApiResponse.success("상태 '" + (status == null ? "전체" : status) + "' 차량 조회 완료", resultDtos);
    }

    // 클래스에 @RequestMapping("/api/cars") 가 있다고 가정
    @GetMapping("/locations")
    public ApiResponse<List<CarLocationDto>> getCarLocations(
            @RequestParam(value = "status", required = false) String status
    ) {
        List<CarLocationDto> locations = carService.getCarLocationsByStatus(status);

        if (status == null || status.isEmpty()) {
            return ApiResponse.success("전체 차량 조회 완료", locations);
        }

        String message = status + " 차량 조회 완료";
        return ApiResponse.success(message, locations);
    }


}
