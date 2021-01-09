package com.swp.ihelp.app.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
    @Query("SELECT e from Event e where e.title like %:title%")
    List<Event> findByTitle(String title);
}

