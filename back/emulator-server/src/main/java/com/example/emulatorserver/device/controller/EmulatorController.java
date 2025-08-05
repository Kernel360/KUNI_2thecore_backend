package com.example.emulatorserver.device.controller;

import com.example.common.domain.emulator.EmulatorEntity;
import com.example.common.dto.ApiResponse;
import com.example.emulatorserver.device.application.EmulatorService;
import com.example.emulatorserver.device.controller.dto.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emulators")
@RequiredArgsConstructor
public class EmulatorController {

    private final EmulatorService emulatorService;
    private final EmulatorConverter emulatorConverter;

    // 애뮬레이터 등록
    @PostMapping
    public ResponseEntity<ApiResponse<RegisterEmulatorResponseData>> registerEmulator(
        @Valid @RequestBody EmulatorRequest emulatorRequest
    ){
        EmulatorEntity savedEntity = emulatorService.registerEmulator(emulatorRequest);
        RegisterEmulatorResponseData responseData = emulatorConverter.toRegisterEmulatorData(savedEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("애뮬레이터가 등록되었습니다.", responseData));
    }


    // 애뮬레이터 상세 조회
    @GetMapping("/{device_id}")
    public ResponseEntity<ApiResponse<GetEmulatorResponseData>> getEmulator(
            @PathVariable("device_id") String deviceId
    ) {
        EmulatorEntity getEntity = emulatorService.getEmulator(deviceId);
        GetEmulatorResponseData responseData = emulatorConverter.toGetEmulatorData(getEntity);

        return ResponseEntity.ok(ApiResponse.success(null, responseData));
    }

    // 애뮬레이터 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<CustomPageResponse<GetEmulatorResponseData>>> getAllEmulators(
            @PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<EmulatorEntity> emulatorsPage = emulatorService.getAllEmulators(pageable);
        Page<GetEmulatorResponseData> dtoPage = emulatorsPage.map(emulatorConverter::toGetEmulatorData);
        CustomPageResponse<GetEmulatorResponseData> responseData = new CustomPageResponse<>(dtoPage);

        return ResponseEntity.ok(ApiResponse.success(null, responseData));
    }

    // 애뮬레이터 수정
    @PatchMapping("/{device_id}")
    public ResponseEntity<ApiResponse<UpdateEmulatorResponseData>> updateEmulator(
            @PathVariable("device_id") String deviceId,
            @Valid @RequestBody EmulatorRequest emulatorRequest
    ){
        EmulatorEntity updatedEntity = emulatorService.updateEmulator(deviceId, emulatorRequest);
        UpdateEmulatorResponseData responseData = emulatorConverter.toUpdateEmulatorData(updatedEntity);

        return ResponseEntity.ok(ApiResponse.success("애뮬레이터 정보가 수정되었습니다.", responseData));
    }

    // 애뮬레이터 삭제
    @DeleteMapping("/{device_id}")
    public ResponseEntity<ApiResponse<DeleteEmulatorResponseData>> deleteEmulator(
            @PathVariable("device_id") String deviceId
    ){
        emulatorService.deleteEmulator(deviceId);
        DeleteEmulatorResponseData responseData = DeleteEmulatorResponseData.builder()
                .deviceId(deviceId)
                .build();

        return ResponseEntity.ok(ApiResponse.success("애뮬레이터가 삭제되었습니다.", responseData));
    }
}
