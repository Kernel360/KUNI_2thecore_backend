package hub.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gps_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GpsLogEntity {
    @Id
    @Column(name = "gps_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String latitude;

    private String longitude;

    private LocalDateTime createdAt;

    private String carNumber;

    public GpsLogEntity(String carNumber, String latitude, String longitude, LocalDateTime createdAt) {
        this.carNumber = carNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }
}
