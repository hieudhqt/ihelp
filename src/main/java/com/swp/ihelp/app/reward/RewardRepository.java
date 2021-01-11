package com.swp.ihelp.app.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, String> {
    @Query("SELECT r from Reward r where r.title like %:title%")
    List<Reward> findByTitle(String title);
}
