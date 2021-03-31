package com.swp.ihelp.app.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, String> {

    @Query(value = "UPDATE feedback b SET b.status_id=:statusId WHERE b.id=:feedbackId", nativeQuery = true)
    void updateStatus(String feedbackId, String statusId) throws Exception;

    @Query("SELECT f FROM FeedbackEntity f WHERE f.status.id=:statusId")
    List<FeedbackEntity> findByStatus(String statusId);

}
