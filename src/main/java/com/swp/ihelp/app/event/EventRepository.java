package com.swp.ihelp.app.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String> {
    @Query("SELECT e from EventEntity e where e.title like %:title%")
    List<EventEntity> findByTitle(String title);

    @Query("SELECT e from EventEntity e where e.eventCategory.id = :categoryId")
    List<EventEntity> findByCategoryId(int categoryId);

    @Query("SELECT e from EventEntity e where e.status.id = :statusId")
    List<EventEntity> findByStatusId(int statusId);

    @Query("SELECT e from EventEntity e where e.authorAccount.email = :email")
    List<EventEntity> findByAuthorEmail(String email);
}

