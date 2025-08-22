package com.example.mainserver.dashboard.infrastructure;

import com.example.mainserver.drivelog.domain.DriveLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<DriveLog, Long> {

    @Query("""
            select dl.model from DriveLog dl where dl.startTime >= :start AND dl.endTime < :end
            GROUP BY dl.model ORDER BY COUNT(dl.model) DESC
""")
    List<String> findTopCars(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,  Pageable pageable);


    @Query("""
    select dl.endPoint from DriveLog dl where dl.startTime >= :start AND dl.endTime < :end
    GROUP BY dl.endPoint ORDER BY COUNT(dl.endPoint) DESC
""")
    List<String> findTopEndPoint(@Param("start")  LocalDateTime start, @Param("end") LocalDateTime end,  Pageable pageable);

    @Query("""
    SELECT c.carType from DriveLog dl Join Car c ON dl.carId = c.id where dl.startTime >= :start AND dl.endTime < :end
    GROUP BY c.carType ORDER BY COUNT(c.carType) DESC
""")
    List<String> findTopCarType(@Param("start")   LocalDateTime start, @Param("end")  LocalDateTime end,   Pageable pageable);
}
