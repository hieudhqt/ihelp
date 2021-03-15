package com.swp.ihelp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    @Query("SELECT s from ServiceEntity s order by s.status.id")
    Page<ServiceEntity> findAll(Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.title like %:title%")
    Page<ServiceEntity> findByTitle(String title, Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.serviceType.id = :id")
    Page<ServiceEntity> findByServiceTypeId(int id, Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.status.id = :id")
    Page<ServiceEntity> findByServiceStatusId(int id, Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.authorAccount.email = :email")
    Page<ServiceEntity> findByAuthorEmail(String email, Pageable pageable);

    @Query("SELECT count(s.service.authorAccount) from ServiceHasAccountEntity s where s.service.id = :serviceId")
    int getRemainingSpots(String serviceId);

    @Query("SELECT s.quota from ServiceEntity s where s.id = :serviceId")
    int getQuota(String serviceId);

    @Modifying
    @Query(value = "UPDATE ihelp.service s SET s.status_id = :statusId WHERE s.id = :serviceId",
            nativeQuery = true)
    void updateStatus(String serviceId, int statusId);
}
