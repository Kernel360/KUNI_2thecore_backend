package com.example.emulatorserver.emulator.controller;

import com.example.emulatorserver.common.dto.ApiResponse;
import com.example.emulatorserver.device.application.EmulatorService;
import com.example.emulatorserver.device.controller.EmulatorController;
import com.example.emulatorserver.device.controller.dto.*;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.exception.emulator.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmulatorControllerTest {

    @Mock
    private EmulatorService emulatorService;

    @Mock
    private EmulatorConverter emulatorConverter;

    @InjectMocks
    private EmulatorController emulatorController;

    private EmulatorRequest emulatorRequest;
    private EmulatorEntity emulatorEntity;
    private GetEmulatorResponseData getEmulatorResponseData;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        emulatorRequest = new EmulatorRequest();
        emulatorRequest.setCarNumber("123가 4567");

        mockMvc = MockMvcBuilders.standaloneSetup(emulatorController)
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
    void registerEmulator_success() {
        RegisterEmulatorResponseData responseData = RegisterEmulatorResponseData.builder()
                .deviceId(emulatorEntity.getDeviceId())
                .carNumber(emulatorEntity.getCarNumber())
                .emulatorStatus(emulatorEntity.getStatus())
                .build();

        when(emulatorService.registerEmulator(any(EmulatorRequest.class))).thenReturn(emulatorEntity);
        when(emulatorConverter.toRegisterEmulatorData(any(EmulatorEntity.class))).thenReturn(responseData);

        ResponseEntity<ApiResponse<RegisterEmulatorResponseData>> response = emulatorController.registerEmulator(emulatorRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("애뮬레이터가 등록되었습니다.", response.getBody().getMessage());
        assertEquals(emulatorEntity.getDeviceId(), response.getBody().getData().getDeviceId());
        verify(emulatorService).registerEmulator(any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 차량 없음")
    void registerEmulator_carNotFound() {
        when(emulatorService.registerEmulator(any())).thenThrow(new CarNotFoundException("not found"));

        assertThrows(CarNotFoundException.class, () ->
                emulatorController.registerEmulator(emulatorRequest));
        verify(emulatorService).registerEmulator(any());
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 해당 차량에 이미 애뮬레이터 연결됨")
    void registerEmulator_carAlreadyHasEmulator() {
        when(emulatorService.registerEmulator(any())).thenThrow(new DuplicateEmulatorException("duplicate"));

        assertThrows(DuplicateEmulatorException.class, () ->
                emulatorController.registerEmulator(emulatorRequest));
        verify(emulatorService).registerEmulator(any());
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 성공")
    void getEmulator_success() {
        String deviceId = "a1b2c3d4-test-uuid";
        when(emulatorService.getEmulator(anyString())).thenReturn(emulatorEntity);
        when(emulatorConverter.toGetEmulatorData(any(EmulatorEntity.class))).thenReturn(getEmulatorResponseData);

        ResponseEntity<ApiResponse<GetEmulatorResponseData>> response = emulatorController.getEmulator(deviceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deviceId, response.getBody().getData().getDeviceId());
        verify(emulatorService).getEmulator(deviceId);
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 실패 - 애뮬레이터 없음")
    void getEmulator_notFound() {
        String deviceId = "존재하지 않는 deviceId";
        when(emulatorService.getEmulator(deviceId)).thenThrow(new EmulatorNotFoundException("Not Found"));

        assertThrows(EmulatorNotFoundException.class, () -> emulatorController.getEmulator(deviceId));
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
                .andExpect(jsonPath("$.data.content").isEmpty()) // content가 비어있는 배열인지 확인
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공 - 차량 번호 변경 없을 때")
    void updateEmulator_noCarNumberChange_success() {
        String deviceId = "a1b2c3d4-test-uuid";
        UpdateEmulatorResponseData responseData = UpdateEmulatorResponseData.builder()
                .deviceId(deviceId)
                .carNumber("123가4567")
                .build();

        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class))).thenReturn(emulatorEntity);
        when(emulatorConverter.toUpdateEmulatorData(any(EmulatorEntity.class))).thenReturn(responseData);

        ResponseEntity<ApiResponse<UpdateEmulatorResponseData>> response = emulatorController.updateEmulator(deviceId, emulatorRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("애뮬레이터 정보가 수정되었습니다.", response.getBody().getMessage());
        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공 - 차량 번호 변경 있을 때")
    void updateEmulator_carNumberChange_success() {
        String deviceId = "a1b2c3d4-test-uuid";
        EmulatorRequest newRequest = new EmulatorRequest("789다 9999");
        UpdateEmulatorResponseData responseData = UpdateEmulatorResponseData.builder()
                .deviceId(deviceId)
                .carNumber("789다 9999")
                .build();

         when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class))).thenReturn(emulatorEntity);
         when(emulatorConverter.toUpdateEmulatorData(any(EmulatorEntity.class))).thenReturn(responseData);

         ResponseEntity<ApiResponse<UpdateEmulatorResponseData>> response = emulatorController.updateEmulator(deviceId, newRequest);

         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertEquals("애뮬레이터 정보가 수정되었습니다.", response.getBody().getMessage());
         verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 애뮬레이터 없음")
    void updateEmulator_emulatorNotFound() {
        String deviceId = "not-found-id";
        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class))).thenThrow(new EmulatorNotFoundException("not found"));

        assertThrows(EmulatorNotFoundException.class, () -> emulatorController.updateEmulator(deviceId, emulatorRequest));
        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 차량 없음")
    void updateEmulator_newCarNotFound() {
        String deviceId = "a1b2c3d4-test-uuid";
        when(emulatorService.updateEmulator(eq(deviceId), any(EmulatorRequest.class))).thenThrow(new CarNotFoundException("no car"));

        assertThrows(CarNotFoundException.class, () -> emulatorController.updateEmulator(deviceId, emulatorRequest));
        verify(emulatorService).updateEmulator(eq(deviceId), any(EmulatorRequest.class));
    }

    @Test
    @DisplayName("애뮬레이터 삭제 성공")
    void deleteEmulator_success() {
        String deviceId = "a1b2c3d4-test-uuid";
        doNothing().when(emulatorService).deleteEmulator(anyString());

        ResponseEntity<ApiResponse<DeleteEmulatorResponseData>> response = emulatorController.deleteEmulator(deviceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("애뮬레이터가 삭제되었습니다.", response.getBody().getMessage());
        verify(emulatorService).deleteEmulator(deviceId);
    }

    @Test
    @DisplayName("애뮬레이터 삭제 실패 - 애뮬레이터 없음")
    void deleteEmulator_notFound() {
        String deviceId = "not-found-id";
        doThrow(new EmulatorNotFoundException("not found")).when(emulatorService).deleteEmulator(deviceId);

        assertThrows(EmulatorNotFoundException.class, () ->
                emulatorController.deleteEmulator(deviceId));
        verify(emulatorService).deleteEmulator(deviceId);
    }

}
