package com.swp.ihelp.app.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointRepository extends JpaRepository<PointEntity, String> {
    @Query("SELECT p from PointEntity p " +
            "WHERE p.account.email = :email ORDER BY p.createdDate desc")
    Page<PointEntity> findAllByEmail(String email, Pageable pageable);

    @Query("SELECT p from PointEntity p " +
            "WHERE p.description LIKE %:eventId% AND p.isReceived = true ORDER BY p.amount desc")
    Page<PointEntity> findPointReceivedByEventId(String eventId, Pageable pageable);

    @Query("SELECT SUM (p.amount) FROM PointEntity p " +
            "WHERE p.description LIKE %:serviceId% AND p.isReceived = true")
    Integer getTotalEarnedByServiceId(String serviceId);
}
