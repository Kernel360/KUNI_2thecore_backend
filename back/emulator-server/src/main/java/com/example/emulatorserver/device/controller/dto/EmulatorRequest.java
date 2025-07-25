package com.example.emulatorserver.device.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmulatorRequest {

    @NotNull(message = "차량 번호는 필수입니다.")
    // TODO: car_number -> device_id 임시 변경. 추후 car테이블 머지 후 수정 필요
    private String deviceId;

    // TODO: 임시로 만든 함수
    public void setCarNumber(String s) {
    }
}
