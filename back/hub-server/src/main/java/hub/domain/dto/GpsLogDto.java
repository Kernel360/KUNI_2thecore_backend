package hub.domain.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private String carNumber;
    private List<Gps> logList = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Gps {
        private String latitude;
        private String longitude;
        private LocalDateTime timestamp;
    }
}
