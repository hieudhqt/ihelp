package com.swp.ihelp.app.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String>, JpaSpecificationExecutor<EventEntity> {

    @Query("SELECT e from EventEntity e where e.title like %:title%")
    Page<EventEntity> findByTitle(String title, Pageable pageable);

    @Query(value = "SELECT t1.* from ihelp.event t1 " +
            "INNER JOIN event_category_has_event t2 " +
            "ON t1.id = t2.event_id AND t2.event_category_id = :categoryId", nativeQuery = true)
    Page<EventEntity> findByCategoryId(int categoryId, Pageable pageable);

    @Query("SELECT e from EventEntity e where e.status.id = :statusId")
    Page<EventEntity> findByStatusId(int statusId, Pageable pageable);

    Page<EventEntity> findAllByEventCategories(int categoryId, Specification<EventEntity> specs, Pageable pageable);

    @Query("SELECT e from EventEntity e where e.authorAccount.email = :email")
    Page<EventEntity> findByAuthorEmail(String email, Pageable pageable);

    @Query("SELECT e.event from EventHasAccountEntity e where e.account.email = :email and e.event.status.id = :statusId")
    Page<EventEntity> findByParticipantEmail(String email, int statusId, Pageable pageable);

    @Query("SELECT count(e.event.authorAccount) from EventHasAccountEntity e where e.event.id = :eventId")
    Integer getSpotUsed(String eventId);

    @Query("SELECT e.startDate From EventEntity e")
    List<Timestamp> getAllStartDates();

    @Query(value =
            "SELECT (e.quota - (SELECT count(account_email) " +
                    "            FROM ihelp.event_has_account " +
                    "            Where event_id = :eventId)) AS RemainingSpot " +
                    "FROM ihelp.event e " +
                    "Where e.id = :eventId ", nativeQuery = true)
    Integer getRemainingSpot(String eventId);

    @Query("SELECT e.quota from EventEntity e where e.id = :eventId")
    Integer getQuota(String eventId);

    @Modifying
    @Query(value = "UPDATE ihelp.event e Set e.status_id = :statusId Where e.id = :eventId ", nativeQuery = true)
    void updateStatus(String eventId, int statusId);

    @Query("SELECT DISTINCT 1 " +
            "FROM EventEntity e " +
            "INNER JOIN EventHasAccountEntity ea on e.id = ea.event.id " +
            "WHERE e.startDate <= :date " +
            "AND e.endDate >= :date " +
            "AND ea.account.email = :email ")
    Integer isAccountJoinedAnyEvent(Date date, String email);

    @Query(value = "SELECT e.id, e.end_date FROM ihelp.event e WHERE e.end_date <= :date " +
            "ORDER BY e.end_date DESC Limit 1 ", nativeQuery = true)
    Object[] getNearestEventEndDate(String date);

    @Query(value = "SELECT e.id, e.start_date FROM ihelp.event e WHERE e.start_date >= :date " +
            "ORDER BY e.start_date ASC Limit 1 ", nativeQuery = true)
    Object[] getNearestEventStartDate(String date);

    @Query("SELECT COUNT(e.id.accountEmail) FROM EventHasAccountEntity e WHERE e.id.accountEmail=:email")
    Integer getTotalJoinedEvents(String email);

    @Query("SELECT COUNT(e) FROM EventEntity e WHERE e.authorAccount.email=:email")
    Integer getTotalHostEvents(String email);

    @Query(value = "SELECT e.id " +
            "FROM event e " +
            "JOIN event_has_account ea ON e.id = ea.event_id " +
            "WHERE e.status_id = 4 AND ea.is_evaluated <> 1 AND e.account_email=:email " +
            "GROUP BY e.id", nativeQuery = true)
    List<String> findEvaluateRequiredByAuthorEmail(String email);
}

