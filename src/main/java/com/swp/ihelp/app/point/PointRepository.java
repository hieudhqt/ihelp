package com.swp.ihelp.app.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointRepository extends JpaRepository<PointEntity, String> {
    @Query("SELECT p from PointEntity p " +
            "WHERE p.account.email = :email ORDER BY p.createdDate desc")
    Page<PointEntity> findAllByEmail(String email, Pageable pageable);
}
