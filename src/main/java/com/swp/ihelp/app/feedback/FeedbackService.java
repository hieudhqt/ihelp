package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackEntity insert(FeedbackRequest request) throws Exception;

    List<FeedbackResponse> findAll() throws Exception;

    FeedbackResponse findById(String id) throws Exception;

    void updateStatus(String feedbackId, String statusId) throws Exception;

    List<FeedbackResponse> findByStatus(String statusId) throws Exception;

    List<FeedbackResponse> findByEmail(String email) throws Exception;

    List<FeedbackResponse> findByEventId(String eventId, String statusId) throws Exception;

    List<FeedbackResponse> findByServiceId(String serviceId, String statusId) throws Exception;

    List<FeedbackResponse> getReports() throws Exception;

    void approve(String feedbackId, String managerEmail) throws Exception;

    void reject(RejectFeedbackRequest request) throws Exception;

}
