package com.example.emulatorserver.device.domain.emulator;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "emulator")
public class EmulatorEntity {

    @Id
    @Column(name = "emulator_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId; // 디바이스 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "emul_status", nullable = false)
    private EmulatorStatus status; // 애뮬레이터 상태 (ON/OFF)

    @Transient
    private String carNumber;

    @PrePersist
    public void createDeviceId() {
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
        }
    }

    public EmulatorEntity(EmulatorStatus status) {
        this.status = status;
    }
}
