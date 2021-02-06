package com.swp.ihelp.app.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    @Query("SELECT s from ServiceEntity s where s.title like %:title%")
    List<ServiceEntity> findByTitle(String title);
}
