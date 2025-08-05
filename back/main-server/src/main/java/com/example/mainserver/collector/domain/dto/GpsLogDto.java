package com.example.mainserver.collector.domain.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GpsLogDto {

    @NotNull
    private String carNumber;

    @NotNull
    private String loginId;

    private List<Gps> logList = new ArrayList<>();

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Gps {

        @NotNull
        private String latitude;

        @NotNull
        private String longitude;

        @NotNull
        private LocalDateTime timestamp;
    }
}
