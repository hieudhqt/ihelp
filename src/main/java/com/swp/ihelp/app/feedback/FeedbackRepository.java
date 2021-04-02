package com.swp.ihelp.app.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, String> {

    @Query(value = "UPDATE feedback b SET b.status_id=:statusId WHERE b.id=:feedbackId", nativeQuery = true)
    void updateStatus(String feedbackId, String statusId) throws Exception;

    @Query("SELECT f FROM FeedbackEntity f WHERE f.status.id=:statusId")
    List<FeedbackEntity> findByStatus(String statusId);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event.id=:eventId")
    List<FeedbackEntity> findByEventId(String eventId);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.service.id=:serviceId")
    List<FeedbackEntity> findByServiceId(String serviceId);

    @Query("SELECT f FROM FeedbackEntity f WHERE f.event IS NULL AND f.service IS NULL")
    List<FeedbackEntity> getReports();

    @Query("SELECT f FROM FeedbackEntity f WHERE f.account.email=:email AND f.event.id=:eventId AND f.service.id=:serviceId")
    List<FeedbackEntity> findByEmail(String email, String eventId, String serviceId) throws Exception;

}
