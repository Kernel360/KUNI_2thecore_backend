package com.example.mainserver.car.controller;

import com.example.common.dto.CarRequestDto;
import com.example.mainserver.car.application.CarService;
import com.example.mainserver.car.controller.dto.*;
import com.example.common.domain.car.CarStatus;
import com.example.mainserver.car.exception.CarAlreadyExistsException;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.example.mainserver.auth.infrastructure.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CarController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
//@SpringBootTest(classes = MainApplication.class)
//@AutoConfigureMockMvc(addFilters = false)
public class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST - 차량 등록 컨트롤러 Test")
    void createCarSuccess() throws Exception {
        // 요청 객체 생성
        CarRequestDto request = CarRequestDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2025)
                .carType("중형")
                .carNumber("12가3456")
                .sumDist(1234.56)
                .build();

        // 응답 객체 생성
        CarDetailDto response = CarDetailDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2025)
                .status("대기")
                .carType("중형")
                .carNumber("12가3456")
                .sumDist(1234.56)
                .build();

        // 기대 return 형식 지정
        when(carService.createCar(any(CarRequestDto.class)))
                .thenReturn(response);

        // test 실행
        ResultActions actions = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // 결과 검증
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.brand").value("현대"))
                .andExpect(jsonPath("$.data.model").value("아반떼"))
                .andExpect(jsonPath("$.data.carYear").value(2025))
                .andExpect(jsonPath("$.data.status").value("대기"))
                .andExpect(jsonPath("$.data.carType").value("중형"))
                .andExpect(jsonPath("$.data.carNumber").value("12가3456"))
                .andExpect(jsonPath("$.data.sumDist").value(1234.56));
    }

    @Test
    @DisplayName("PATCH - 차량 정보 수정 컨트롤러 Test")
    void updateCarSuccess() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .carYear(2015)
                .status("운행")
                .carNumber("6543나21")
                .build();

        CarDetailDto response = CarDetailDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2015)
                .status("운행")
                .carType("중형")
                .carNumber("6543나21")
                .sumDist(1234.56)
                .build();

        when(carService.updateCar(any(CarRequestDto.class), any(String.class)))
                .thenReturn(response);

        ResultActions actions = mockMvc.perform(patch("/api/cars/1234가56")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.carYear").value(2015))
                .andExpect(jsonPath("$.data.status").value("운행"))
                .andExpect(jsonPath("$.data.carNumber").value("6543나21"));
    }

    @Test
    @DisplayName("DELETE - 차량 삭제 컨트롤러 Test")
    void deleteCarSuccess() throws Exception {
        CarDeleteDto response = CarDeleteDto.builder()
                .brand("현대")
                .model("아반떼")
                .carNumber("6543나21")
                .build();

        when(carService.deleteCar(any(String.class)))
                .thenReturn(response);

        ResultActions actions = mockMvc.perform(delete("/api/cars/6543나21"));

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.brand").value("현대"))
                .andExpect(jsonPath("$.data.model").value("아반떼"))
                .andExpect(jsonPath("$.data.carNumber").value("6543나21"));
    }

    // 예외처리 Test Code
    @Test
    @DisplayName("POST - 차량 등록 실패: 차량 번호 중복")
    void createCarFailCarNum() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2025)
                .carType("중형")
                .carNumber("12가3456")
                .sumDist(1234.56)
                .build();

        when(carService.createCar(any(CarRequestDto.class)))
                .thenThrow(new CarAlreadyExistsException("12가3456"));

        ResultActions actions = mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
//                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
//                .andExpect(jsonPath("$.data.message").value("이미 등록된 차량입니다: 12가3456"))
//                .andExpect(jsonPath("$.data.path").value("/api/cars"));
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("이미 등록된 차량입니다: 12가3456"));
    }

    @Test
    @DisplayName("PATCH - 차량 정보 수정 실패: 존재하지 않는 차량")
    void updateCarFailNotFound() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .carYear(2015)
                .status("운행")
                .carNumber("6543나21")
                .build();

        when(carService.updateCar(any(CarRequestDto.class), any(String.class)))
                .thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, "1234가56"));

        ResultActions actions = mockMvc.perform(patch("/api/cars/1234가56")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.data.status").value(HttpStatus.NOT_FOUND.value()))
//                .andExpect(jsonPath("$.data.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
//                .andExpect(jsonPath("$.data.message").value("해당 차량 ( 1234가56 )은 존재하지 않습니다. 다시 입력해주세요"));
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("해당 차량 ( 1234가56 )은 존재하지 않습니다. 다시 입력해주세요"));
    }

    @Test
    @DisplayName("PATCH - 차량 정보 수정 실패: 이미 존재하는 차량 번호")
    void updateCarFailAlreadyExists() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .carYear(2015)
                .status("운행")
                .carNumber("6543나21")
                .build();

        when(carService.updateCar(any(CarRequestDto.class), any(String.class)))
                .thenThrow(new CarAlreadyExistsException("6543나21"));

        ResultActions actions = mockMvc.perform(patch("/api/cars/1234가56")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
//                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
//                .andExpect(jsonPath("$.data.message").value("이미 등록된 차량입니다: 6543나21"));
        .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("이미 등록된 차량입니다: 6543나21"));
    }

    @Test
    @DisplayName("DELETE - 차량 삭제 실패: 존재하지 않는 차량")
    void deleteCarFail() throws Exception {
        when(carService.deleteCar("9999가99"))
                .thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, "9999가99"));

        ResultActions actions = mockMvc.perform(delete("/api/cars/9999가99"));

        actions
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.data.status").value(HttpStatus.NOT_FOUND.value()))
//                .andExpect(jsonPath("$.data.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
//                .andExpect(jsonPath("$.data.message").value("해당 차량 ( 9999가99 )은 존재하지 않습니다. 다시 입력해주세요"));
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("해당 차량 ( 9999가99 )은 존재하지 않습니다. 다시 입력해주세요"));
    }

    @Test
    @DisplayName("차량 번호를 통해 해당 차량을 조회한다.")
    public void getCarByCarNumber() throws Exception {
        var response = CarDetailDto.builder()
                .brand("현대")
                .model("아이오닉")
                .carNumber("12가1234")
                .build();

        when(carService.getCar("12가1234")).thenReturn(response);

        mockMvc.perform(get("/api/cars/12가1234"))
                .andDo(print())
                .andExpect(jsonPath("$.data.carNumber").value("12가1234"))
                .andExpect(jsonPath("$.data.model").value("아이오닉"));


        when(carService.getCar("1234")).thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER,"1234"));

        mockMvc.perform(get("/api/cars/1234"))
                .andDo(print())
//                .andExpect(jsonPath("$.data.status").value(404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 차량 ( 1234 )은 존재하지 않습니다. 다시 입력해주세요"));
    }



    @Test
    @DisplayName("차량 상태에 따른 대시보드 테스트 출력")
    public void getCarByStatus() throws Exception {

        List<CarSearchDto> carList = List.of(new CarSearchDto("12가1234", "현대", "아이오닉", "IN_IDLE")
                ,new CarSearchDto("12가3423", "기아", "아이오닉", "IN_IDLE"),
                new CarSearchDto("12가2315", "기아", "그렌저", "MAINTENANCE"));

        var response = CarSummaryDto.builder()
                .total(3L)
                .driving(0L)
                .idle(2L)
                .maintenence(1L)
                .build();

        when(carService.getCountByStatus()).thenReturn(response);

        mockMvc.perform(get("/api/cars/statistics"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.total").value(3L))
                .andExpect(jsonPath("$.data.waiting").value(2L))
                .andExpect(jsonPath("$.data.inspecting").value(1L))
                .andExpect(jsonPath("$.data.operating").value(0L));
    }


    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 1st: 아무조건 없음")
    public void getAllCarsByFilter1st() throws Exception {

        var requestDto = CarFilterRequestDto.builder()
                .twoParam(false)
                .build();

        List<CarSearchDto> carList = List.of(new CarSearchDto("12가1234", "현대", "아이오닉", "IDLE")
                ,new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"),
                new CarSearchDto("12가2315", "기아", "그렌저", "MAINTENANCE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carList, PageRequest.of(0, 4), 3);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].carNumber").value("12가1234"))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 2nd: 차량번호")
    public void getAllCarsByFilter2nd() throws Exception {

        // 차량 번호만 입력
        var requestDto_2 = CarFilterRequestDto.builder()
                .carNumber("12가3423")
                .twoParam(false)
                .build();
        List<CarSearchDto> carByNumber = List.of(new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carByNumber, PageRequest.of(0, 4), 1);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].carNumber").value("12가3423"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));}

    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 3rd: 차량 상태")
    public void getAllCarsByFilter3nd() throws Exception {

        // 차량 상태만 입력
        var requestDto_4 = CarFilterRequestDto.builder()
                .status(CarStatus.IDLE)
                .twoParam(false)
                .build();
        List<CarSearchDto> carByStatusInIdle = List.of(new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE")
                , new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carByStatusInIdle, PageRequest.of(0, 4), 2);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].status").value(CarStatus.IDLE.toString()))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 4th: 브랜드 + 모델명")
    public void getAllCarsByFilter4th() throws Exception {

        // brand, model명 필터링
        var requestDto_4 = CarFilterRequestDto.builder()
                .model("아이오닉")
                .brand("기아")
                .twoParam(true)
                .build();

        List<CarSearchDto> carByBrandAndModel = List.of(new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carByBrandAndModel, PageRequest.of(0, 4), 1);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].brand").value("기아"))
                .andExpect(jsonPath("$.data.content[0].model").value("아이오닉"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 5th: 브랜드 >> 브랜드")
    public void getAllCarsByFilter5th() throws Exception {
//
        // 브랜드명으로 브랜드명이 옴
        var requestDto_6 = CarFilterRequestDto.builder()
                .brand("기아")
                .twoParam(false)
                .build();

        List<CarSearchDto> carByBrandToBrand = List.of(new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"),
                new CarSearchDto("12가3423", "기아", "아이오닉", "IN_USE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carByBrandToBrand, PageRequest.of(0, 4), 2);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].brand").value("기아"))
                .andExpect(jsonPath("$.data.content[1].brand").value("기아"))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("필터링 조건에 기반하여 차량 조회 6th: 모델 >> 브랜드")
    public void getAllCarsByFilter6th() throws Exception {

        // 브랜드에 모델명이 들어옴

        var requestDto_7 = CarFilterRequestDto.builder()
                .brand("아이오닉")
                .twoParam(false)
                .build();

        List<CarSearchDto> carByModelToBrand = List.of(new CarSearchDto("12가3423", "기아", "아이오닉", "IDLE"));

        Page<CarSearchDto> pageResult = new PageImpl<>(carByModelToBrand, PageRequest.of(0, 4), 1);


        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/cars/search").param("twoParam", "false")
                        .param("page", "1").param("size", "4"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].brand").value("기아"))
                .andExpect(jsonPath("$.data.content[0].model").value("아이오닉"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(4))
                .andExpect(jsonPath("$.data.number").value(0));

    }
//
    @Test
    @DisplayName("해당 조건을 충족하는 차량이 없는경우")
    public void getCarsByFilterFailed() throws Exception {

        var requestDto_7 = CarFilterRequestDto.builder()
                .model("삼성")
                .twoParam(false)
                .build();


        Page<CarSearchDto> emptyPage = Page.empty(PageRequest.of(0, 5));

        when(carService.getCarsByFilter(any(CarFilterRequestDto.class), anyInt(), anyInt()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/cars/search").param("model", "삼성").param("twoParam", "false"))
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.data.number").value(0));
    }
}
