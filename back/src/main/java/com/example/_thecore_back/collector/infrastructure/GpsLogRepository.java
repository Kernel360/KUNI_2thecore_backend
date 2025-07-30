package com.example._thecore_back.collector.infrastructure;

import com.example._thecore_back.collector.domain.GpsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpsLogRepository extends JpaRepository<GpsLogEntity, Integer> {
}
