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
            "WHERE p.event.id = :eventId AND p.isReceived = true AND p.account.email <> :author ORDER BY p.amount desc")
    Page<PointEntity> findPointReceivedByEventId(String eventId, String author, Pageable pageable);


    @Query("SELECT SUM(p.amount) from PointEntity p " +
            "WHERE p.event.id = :eventId AND p.isReceived = true")
    Integer findSumPointReceivedByEventId(String eventId);

    @Query("SELECT SUM (p.amount) FROM PointEntity p " +
            "WHERE p.service.id = :serviceId AND p.isReceived = true")
    Integer getTotalEarnedByServiceId(String serviceId);
}
