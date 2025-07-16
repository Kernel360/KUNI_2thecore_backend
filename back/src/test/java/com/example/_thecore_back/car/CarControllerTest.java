package com.example._thecore_back.car;

import com.example._thecore_back.car.application.CarService;
import com.example._thecore_back.car.controller.CarController;
import com.example._thecore_back.car.controller.dto.CarDeleteDto;
import com.example._thecore_back.car.controller.dto.CarDetailDto;
import com.example._thecore_back.car.controller.dto.CarRequestDto;
import com.example._thecore_back.car.exception.CarAlreadyExistsException;
import com.example._thecore_back.car.exception.CarNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
public class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

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
                .emulatorId(1)
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
        ResultActions actions = mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // 결과 검증
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.brand").value("현대"))
                .andExpect(jsonPath("$.data.model").value("아반떼"))
                .andExpect(jsonPath("$.data.car_year").value(2025))
                .andExpect(jsonPath("$.data.status").value("대기"))
                .andExpect(jsonPath("$.data.car_type").value("중형"))
                .andExpect(jsonPath("$.data.car_number").value("12가3456"))
                .andExpect(jsonPath("$.data.sum_dist").value(1234.56));
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

        ResultActions actions = mockMvc.perform(patch("/api/vehicles/1234가56")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.car_year").value(2015))
                .andExpect(jsonPath("$.data.status").value("운행"))
                .andExpect(jsonPath("$.data.car_number").value("6543나21"));
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

        ResultActions actions = mockMvc.perform(delete("/api/vehicles/6543나21"));

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.brand").value("현대"))
                .andExpect(jsonPath("$.data.model").value("아반떼"))
                .andExpect(jsonPath("$.data.car_number").value("6543나21"));
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
                .emulatorId(1)
                .build();

        when(carService.createCar(any(CarRequestDto.class)))
                .thenThrow(new CarAlreadyExistsException("12가3456", null));

        ResultActions actions = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("이미 등록된 차량 번호입니다: 12가3456"))
                .andExpect(jsonPath("$.data.path").value("/api/vehicles"));
    }

    @Test
    @DisplayName("POST - 차량 등록 실패: 애뮬레이터 번호 중복")
    void createCarFailEmulatorNum() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2025)
                .carType("중형")
                .carNumber("12가3456")
                .sumDist(1234.56)
                .emulatorId(1)
                .build();

        when(carService.createCar(any(CarRequestDto.class)))
                .thenThrow(new CarAlreadyExistsException(null, 1));

        ResultActions actions = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("이미 등록된 에뮬레이터 ID입니다: 1"))
                .andExpect(jsonPath("$.data.path").value("/api/vehicles"));
    }

    @Test
    @DisplayName("POST - 차량 등록 실패: 차량 번호 및 애뮬레이터 ID 중복")
    void createCarFailBoth() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .brand("현대")
                .model("아반떼")
                .carYear(2025)
                .carType("중형")
                .carNumber("12가3456")
                .sumDist(1234.56)
                .emulatorId(1)
                .build();

        when(carService.createCar(any(CarRequestDto.class)))
                .thenThrow(new CarAlreadyExistsException("12가3456", 1));

        ResultActions actions = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("이미 등록된 차량입니다. (차량 번호: 12가3456, 에뮬레이터 ID: 1)"))
                .andExpect(jsonPath("$.data.path").value("/api/vehicles"));
    }

    @Test
    @DisplayName("PATCH - 차량 정보 수정 실패: 애뮬레이터 번호 중복")
    void updateCarFailEmulatorNum() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .carYear(2015)
                .status("운행")
                .carNumber("6543나21")
                .emulatorId(1)
                .build();

        when(carService.updateCar(any(CarRequestDto.class), any(String.class)))
                .thenThrow(new CarAlreadyExistsException(null, 1));

        ResultActions actions = mockMvc.perform(patch("/api/vehicles/1234가56")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("이미 등록된 에뮬레이터 ID입니다: 1"));
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
                .thenThrow(new CarNotFoundException("1234가56"));

        ResultActions actions = mockMvc.perform(patch("/api/vehicles/1234가56")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("해당 차량 ( 1234가56 )은 존재하지 않습니다. 다시 입력해주세요"));
    }

    @Test
    @DisplayName("DELETE - 차량 삭제 실패: 존재하지 않는 차량")
    void deleteCarFail() throws Exception {
        when(carService.deleteCar("9999가99"))
                .thenThrow(new CarNotFoundException("9999가99"));

        ResultActions actions = mockMvc.perform(delete("/api/vehicles/9999가99"));

        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.data.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.data.message").value("해당 차량 ( 9999가99 )은 존재하지 않습니다. 다시 입력해주세요"));
    }
}
