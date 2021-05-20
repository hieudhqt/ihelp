package com.swp.ihelp.app.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, String> {

    @Query(value = "UPDATE feedback b SET b.status_id=:statusId WHERE b.id=:feedbackId", nativeQuery = true)
    void updateStatus(String feedbackId, String statusId) throws Exception;

    boolean existsByEvent_IdAndAccount_Email(String eventId, String accountEmail) throws Exception;

    boolean existsByService_IdAndAccount_Email(String serviceId, String accountEmail) throws Exception;

    @Query("SELECT f FROM FeedbackEntity f WHERE f.status.id=:statusId")
    Page<FeedbackEntity> findByStatus(Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event.id=:eventId AND f.feedbackCategory.id=:categoryId AND f.status.id=:statusId")
    Page<FeedbackEntity> findByEventIdWithStatusId(String eventId, Integer categoryId, Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event.id=:eventId AND f.feedbackCategory.id=:categoryId")
    Page<FeedbackEntity> findByEventId(String eventId, Integer categoryId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.service.id=:serviceId AND f.feedbackCategory.id=:categoryId AND f.status.id=:statusId")
    Page<FeedbackEntity> findByServiceIdWithStatusId(String serviceId, Integer categoryId, Integer statusId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.service.id=:serviceId AND f.feedbackCategory.id=:categoryId")
    Page<FeedbackEntity> findByServiceId(String serviceId, Integer categoryId, Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.feedbackCategory.id = 7")
    Page<FeedbackEntity> getReports(Pageable pageable);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.account.email=:email AND (f.event IS NOT NULL OR f.service IS NOT NULL)")
    Page<FeedbackEntity> findByEmail(String email, Pageable pageable) throws Exception;

}