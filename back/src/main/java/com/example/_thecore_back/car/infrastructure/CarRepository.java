package com.example._thecore_back.car.infrastructure;

import com.example._thecore_back.car.domain.CarEntity;
import com.example._thecore_back.car.domain.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("mainCarRepository")
public interface CarRepository extends JpaRepository<CarEntity, Integer> {

    Optional<CarEntity> findByCarNumber(String carNumber);

    Optional<CarEntity> findByEmulatorId(Integer emulatorId);

    List<CarEntity> findByStatusIn(List<CarStatus> statuses); // 차량 상태 리스트 조회

    @Query("select c.status, count(c) from Car c group by c.status")
    List<Object[]> getCountByStatus();


}