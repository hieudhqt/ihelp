package com.swp.ihelp.app.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    @Query("SELECT s from ServiceEntity s where s.title like %:title%")
    List<ServiceEntity> findByTitle(String title);

    @Query("SELECT s from ServiceEntity s where s.serviceTypeByServiceTypeId.id = :id")
    List<ServiceEntity> findByServiceTypeId(int id);

    @Query("SELECT s from ServiceEntity s where s.statusByStatusId.id = :id")
    List<ServiceEntity> findByServiceStatusId(int id);

    @Query("SELECT s from ServiceEntity s where s.accountByAccountEmail.email = :email")
    List<ServiceEntity> findByAuthorEmail(String email);
}
