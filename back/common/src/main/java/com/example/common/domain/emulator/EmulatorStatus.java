package com.example.common.domain.emulator;

import lombok.Getter;

@Getter
public enum EmulatorStatus {
    ON("ON"),
    OFF("OFF");

    private final String displayName;

    EmulatorStatus(String displayName) {
        this.displayName = displayName;
    }

    // StringToStatus
    public static EmulatorStatus getEmulatorStatus(String displayName) {
        for (EmulatorStatus emulatorStatus : EmulatorStatus.values()) {
            if (emulatorStatus.getDisplayName().equals(displayName)) {
                return emulatorStatus;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 status: " + displayName);
    }
}
