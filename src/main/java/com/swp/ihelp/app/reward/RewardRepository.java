package com.swp.ihelp.app.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RewardRepository extends JpaRepository<RewardEntity, String> {
    @Query(value = "SELECT SUM(r.point) " +
            "FROM ihelp.reward r " +
            "WHERE r.account_email = :email " +
            "AND DATE(r.created_date) >= :startDate " +
            "AND DATE(r.created_date) <= :endDate ", nativeQuery = true)
    Integer getTotalPointByDate(String email, String startDate, String endDate) throws Exception;

    @Query("SELECT r from RewardEntity r " +
            "WHERE r.title LIKE %:eventId% AND r.account.email = :email")
    RewardEntity findRewardByEventIdAndEmail(String eventId, String email);

    @Query(value = "Select CASE WHEN COUNT(r.id) = 1 THEN true ELSE false END " +
            "FROM RewardEntity r WHERE r.account.email = :email")
    Boolean isAccountContributed(String email);
}
