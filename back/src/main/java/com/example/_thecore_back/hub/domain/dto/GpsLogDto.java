package com.example._thecore_back.hub.domain.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GpsLogDto {

    @NotNull
    private String carNumber;

    private List<Gps> logList = new ArrayList<>();

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Gps {

        @NotNull
        private String latitude;

        @NotNull
        private String longitude;

        @NotNull
        private LocalDateTime timestamp;
    }
}
