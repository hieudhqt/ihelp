package com.swp.ihelp.app.servicevolunteer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceVolunteerRepository extends JpaRepository<ServiceVolunteer, String> {
    @Query("SELECT s from ServiceVolunteer s where s.title like %:title%")
    List<ServiceVolunteer> findByTitle(String title);
}

