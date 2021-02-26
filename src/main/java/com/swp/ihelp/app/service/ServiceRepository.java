package com.swp.ihelp.app.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    @Query("SELECT s from ServiceEntity s where s.title like %:title%")
    List<ServiceEntity> findByTitle(String title);

    @Query("SELECT s from ServiceEntity s where s.serviceType.id = :id")
    List<ServiceEntity> findByServiceTypeId(int id);

    @Query("SELECT s from ServiceEntity s where s.status.id = :id")
    List<ServiceEntity> findByServiceStatusId(int id);

    @Query("SELECT s from ServiceEntity s where s.authorAccount.email = :email")
    List<ServiceEntity> findByAuthorEmail(String email);

    @Query("SELECT count(s.service.authorAccount) from ServiceHasAccountEntity s where s.service.id = :serviceId")
    int getRemainingSpots(String serviceId);

    @Query("SELECT s.quota from ServiceEntity s where s.id = :serviceId")
    int getQuota(String serviceId);
}
