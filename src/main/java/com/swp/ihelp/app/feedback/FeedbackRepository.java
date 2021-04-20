package com.swp.ihelp.app.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, String> {

    @Query(value = "UPDATE feedback b SET b.status_id=:statusId WHERE b.id=:feedbackId", nativeQuery = true)
    void updateStatus(String feedbackId, String statusId) throws Exception;

    @Query("SELECT f FROM FeedbackEntity f WHERE f.status.id=:statusId")
    Page<FeedbackEntity> findByStatus(Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event.id=:eventId AND f.status.id=:statusId")
    Page<FeedbackEntity> findByEventIdWithStatusId(String eventId, Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event.id=:eventId")
    Page<FeedbackEntity> findByEventId(String eventId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.service.id=:serviceId AND f.status.id=:statusId")
    Page<FeedbackEntity> findByServiceIdWithStatusId(String serviceId, Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.service.id=:serviceId")
    Page<FeedbackEntity> findByServiceId(String serviceId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event IS NULL AND f.service IS NULL")
    Page<FeedbackEntity> getReports(Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.account.email=:email AND f.event IS NOT NULL OR f.service IS NOT NULL")
    Page<FeedbackEntity> findByEmail(String email, Pageable pageable) throws Exception;

}