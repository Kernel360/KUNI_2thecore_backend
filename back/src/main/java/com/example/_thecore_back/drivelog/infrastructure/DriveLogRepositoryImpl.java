package com.example._thecore_back.drivelog.infrastructure;

import com.example._thecore_back.drivelog.domain.DriveLog;
import com.example._thecore_back.drivelog.domain.DriveLogRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DriveLogRepositoryImpl implements DriveLogRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<DriveLog> searchByConditions(Long carId, LocalDateTime startTime, LocalDateTime endTime) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DriveLog> cq = cb.createQuery(DriveLog.class);
        Root<DriveLog> root = cq.from(DriveLog.class);

        List<Predicate> predicates = new ArrayList<>();

        if (carId != null) {
            predicates.add(cb.equal(root.get("carId"), carId));
        }

        if (startTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startTime));
        }

        if (endTime != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<DriveLog> query = em.createQuery(cq);
        return query.getResultList();
    }
}
