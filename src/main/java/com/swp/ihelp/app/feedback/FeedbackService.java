package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;

import java.util.Map;

public interface FeedbackService {

    FeedbackEntity insert(FeedbackRequest request) throws Exception;

    Map<String, Object> findAll(int page) throws Exception;

    FeedbackResponse findById(String id) throws Exception;

    void updateStatus(String feedbackId, String statusId) throws Exception;

    Map<String, Object> findByStatus(Integer statusId, int page) throws Exception;

    Map<String, Object> findByEmail(String email, int page) throws Exception;

    Map<String, Object> findByEventId(String eventId, Integer statusId, int page) throws Exception;

    Map<String, Object> findByServiceId(String serviceId, Integer statusId, int page) throws Exception;

    Map<String, Object> getReports(int page) throws Exception;

    void approve(String feedbackId, String managerEmail) throws Exception;

    void reject(RejectFeedbackRequest request) throws Exception;

}