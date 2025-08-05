package com.example.mainserver.admin.controller;

import com.example.common.dto.ApiResponse;
import com.example.mainserver.admin.controller.dto.AdminRequest;
import com.example.mainserver.admin.controller.dto.AdminResponse;
import com.example.mainserver.admin.application.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    //회원가입 api
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AdminResponse>> registerAdmin(@RequestBody AdminRequest requestDto) {
        AdminResponse responseDto = adminService.registerAdmin(requestDto);
        ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                .result(true)
                .data(responseDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //회원 정보 수정 api
    @PutMapping("/{loginId}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(@PathVariable String loginId, @RequestBody AdminRequest requestDto) {
        AdminResponse responseDto = adminService.updateAdmin(loginId, requestDto);
        ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                .result(true)
                .data(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }

    //회원 삭제 api
    @DeleteMapping("/{loginId}")
    public ResponseEntity<ApiResponse<String>> deleteAdmin(@PathVariable String loginId) {
        String deletedId = adminService.deleteAdmin(loginId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .result(true)
                .message(deletedId + " admin deleted successfully.")
                .build();
        return ResponseEntity.ok(response);
    }
}