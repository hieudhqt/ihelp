package com.swp.ihelp.app.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<EventEntity, String> {
    Page<EventEntity> findAll(Pageable pageable);

    @Query("SELECT e from EventEntity e where e.title like %:title%")
    Page<EventEntity> findByTitle(String title, Pageable pageable);

    @Query("SELECT e from EventEntity e where e.eventCategory.id = :categoryId")
    Page<EventEntity> findByCategoryId(int categoryId, Pageable pageable);

    @Query("SELECT e from EventEntity e where e.status.id = :statusId")
    Page<EventEntity> findByStatusId(int statusId, Pageable pageable);

    @Query("SELECT e from EventEntity e where e.authorAccount.email = :email")
    Page<EventEntity> findByAuthorEmail(String email, Pageable pageable);

    @Query("SELECT e.event from EventHasAccountEntity e where e.account.email = :email and e.event.status.id = :statusId")
    Page<EventEntity> findByParticipantEmail(String email, int statusId, Pageable pageable);

    @Query("SELECT count(e.event.authorAccount) from EventHasAccountEntity e where e.event.id = :eventId")
    int getRemainingSpots(String eventId);

    @Query("SELECT e.quota from EventEntity e where e.id = :eventId")
    int getQuota(String eventId);

    @Modifying
    @Query("UPDATE EventEntity e Set e.status.id = :statusId Where e.id = :eventId ")
    void updateStatus(String eventId, int statusId);
}

