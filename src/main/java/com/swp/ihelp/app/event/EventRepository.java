package com.swp.ihelp.app.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String> {
    @Query("SELECT e from EventEntity e where e.title like %:title%")
    List<EventEntity> findByTitle(String title);
}

