package com.example.mainserver.collector.infrastructure;

import com.example.mainserver.collector.domain.GpsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpsLogRepository extends JpaRepository<GpsLogEntity, Integer> {
}
