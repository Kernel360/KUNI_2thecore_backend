package com.example.emulatorserver.device.controller;

import com.example.emulatorserver.device.application.EmulatorService;
import com.example.emulatorserver.device.controller.dto.*;
import com.example.emulatorserver.device.domain.EmulatorEntity;
import com.example.common.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;
import com.example.emulatorserver.device.exception.emulator.EmulatorErrorCode;
import com.example.emulatorserver.device.exception.emulator.EmulatorExceptionHandler;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EmulatorControllerTest {

    @Mock
    private EmulatorService emulatorService;

    @Mock
    private EmulatorConverter emulatorConverter;

    @InjectMocks
    private EmulatorController emulatorController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private EmulatorRequest emulatorRequest;
    private EmulatorEntity emulatorEntity;
    private GetEmulatorResponseData getEmulatorResponseData;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        emulatorRequest = new EmulatorRequest("123가 4567");

        mockMvc = MockMvcBuilders.standaloneSetup(emulatorController)
                .setControllerAdvice(new EmulatorExceptionHandler()) // Exception Handler 등록
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        emulatorEntity = EmulatorEntity.builder()
                .id(1)
                .deviceId("a1b2c3d4-test-uuid")
                .carNumber("123가 4567")
                .status(EmulatorStatus.OFF)
                .build();

        getEmulatorResponseData = GetEmulatorResponseData.builder()
                .deviceId("a1b2c3d4-test-uuid")
                .carNumber("123가 4567")
                .emulatorStatus(EmulatorStatus.OFF)
                .build();
    }

    @Test
    @DisplayName("애뮬레이터 등록 성공")
    void registerEmulator_success() throws Exception {
        RegisterEmulatorResponseData responseData = RegisterEmulatorResponseData.builder()
                .deviceId(emulatorEntity.getDeviceId())
                .carNumber(emulatorEntity.getCarNumber())
                .emulatorStatus(emulatorEntity.getStatus())
                .build();

        when(emulatorService.registerEmulator(any(EmulatorRequest.class))).thenReturn(emulatorEntity);
        when(emulatorConverter.toRegisterEmulatorData(any(EmulatorEntity.class))).thenReturn(responseData);

        mockMvc.perform(post("/api/emulators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emulatorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("애뮬레이터가 등록되었습니다."))
                .andExpect(jsonPath("$.data.deviceId").value(emulatorEntity.getDeviceId()));

        verify(emulatorService).registerEmulator(any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 차량 없음")
    void registerEmulator_carNotFound() throws Exception {
        String carNumber = "없는차량1234";
        EmulatorRequest request = new EmulatorRequest(carNumber);
        String expectedMessage = CarErrorCode.CAR_NOT_FOUND_BY_NUMBER.format(carNumber);

        when(emulatorService.registerEmulator(any(EmulatorRequest.class)))
                .thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

        mockMvc.perform(post("/api/emulators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).registerEmulator(any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 해당 차량에 이미 애뮬레이터 연결됨")
    void registerEmulator_carAlreadyHasEmulator() throws Exception {
        String carNumber = "123가 4567";
        String expectedMessage = EmulatorErrorCode.DUPLICATE_EMULATOR.format(carNumber);

        when(emulatorService.registerEmulator(any(EmulatorRequest.class)))
                .thenThrow(new DuplicateEmulatorException(EmulatorErrorCode.DUPLICATE_EMULATOR, carNumber));

        mockMvc.perform(post("/api/emulators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emulatorRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).registerEmulator(any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 성공")
    void getEmulator_success() throws Exception {
        String deviceId = "a1b2c3d4-test-uuid";
        when(emulatorService.getEmulator(anyString())).thenReturn(emulatorEntity);
        when(emulatorConverter.toGetEmulatorData(any(EmulatorEntity.class))).thenReturn(getEmulatorResponseData);

        mockMvc.perform(get("/api/emulators/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.deviceId").value(deviceId));

        verify(emulatorService).getEmulator(deviceId);
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 실패 - 애뮬레이터 없음")
    void getEmulator_notFound() throws Exception {
        String deviceId = "존재하지 않는 deviceId";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);

        when(emulatorService.getEmulator(deviceId))
                .thenThrow(new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId));

        mockMvc.perform(get("/api/emulators/{deviceId}", deviceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).getEmulator(deviceId);
    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공 - 데이터가 있을 때")
    void getAllEmulators_success() throws Exception {
        List<EmulatorEntity> emulatorList = Collections.singletonList(emulatorEntity);
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmulatorEntity> emulatorsPage = new PageImpl<>(emulatorList, pageable, emulatorList.size());

        when(emulatorService.getAllEmulators(any(Pageable.class))).thenReturn(emulatorsPage);
        when(emulatorConverter.toGetEmulatorData(any(EmulatorEntity.class))).thenReturn(getEmulatorResponseData);

        mockMvc.perform(get("/api/emulators")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].deviceId").value(getEmulatorResponseData.getDeviceId()))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.last").value(true));

    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공 - 애뮬레이터가 없을 때 빈 페이지 반환")
    void getAllEmulators_emptyPage_success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<EmulatorEntity> emptyList = Collections.emptyList();
        Page<EmulatorEntity> emptyPage = new PageImpl<>(emptyList, pageable, 0);

        when(emulatorService.getAllEmulators(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/emulators")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공")
    void updateEmulator_success() throws Exception {
        String deviceId = "a1b2c3d4-test-uuid";
        UpdateEmulatorResponseData responseData = UpdateEmulatorResponseData.builder()
                .deviceId(deviceId)
                .carNumber("123가4567")
                .build();

        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class))).thenReturn(emulatorEntity);
        when(emulatorConverter.toUpdateEmulatorData(any(EmulatorEntity.class))).thenReturn(responseData);

        mockMvc.perform(patch("/api/emulators/{deviceId}", deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emulatorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("애뮬레이터 정보가 수정되었습니다."));

        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 애뮬레이터 없음")
    void updateEmulator_emulatorNotFound() throws Exception {
        String deviceId = "not-found-id";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);

        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class)))
                .thenThrow(new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId));

        mockMvc.perform(patch("/api/emulators/{deviceId}", deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emulatorRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 차량 없음")
    void updateEmulator_newCarNotFound() throws Exception {
        String deviceId = "a1b2c3d4-test-uuid";
        String newCarNumber = "없는차량9999";
        EmulatorRequest request = new EmulatorRequest(newCarNumber);
        String expectedMessage = CarErrorCode.CAR_NOT_FOUND_BY_NUMBER.format(newCarNumber);

        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class)))
                .thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, newCarNumber));

        mockMvc.perform(patch("/api/emulators/{deviceId}", deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 삭제 성공")
    void deleteEmulator_success() throws Exception {
        String deviceId = "a1b2c3d4-test-uuid";
        doNothing().when(emulatorService).deleteEmulator(anyString());

        mockMvc.perform(delete("/api/emulators/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("애뮬레이터가 삭제되었습니다."));

        verify(emulatorService).deleteEmulator(deviceId);
    }

    @Test
    @DisplayName("애뮬레이터 삭제 실패 - 애뮬레이터 없음")
    void deleteEmulator_notFound() throws Exception {
        String deviceId = "not-found-id";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);

        doThrow(new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId))
                .when(emulatorService).deleteEmulator(deviceId);

        mockMvc.perform(delete("/api/emulators/{deviceId}", deviceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(emulatorService).deleteEmulator(deviceId);
    }
}