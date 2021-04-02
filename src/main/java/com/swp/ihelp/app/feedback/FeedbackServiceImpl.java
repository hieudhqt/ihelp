package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public FeedbackEntity insert(FeedbackRequest request) throws Exception {
        FeedbackEntity feedbackEntity = FeedbackRequest.convertToEntity(request);
        return feedbackRepository.save(feedbackEntity);
    }

    @Override
    public List<FeedbackResponse> findAll() throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAll();
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public FeedbackResponse findById(String id) throws Exception {
        Optional<FeedbackEntity> result = feedbackRepository.findById(id);
        FeedbackEntity feedbackEntity = null;
        if (result.isPresent()) {
            feedbackEntity = result.get();
        } else {
            throw new EntityNotFoundException("Do not find feedback with ID: " + id);
        }
        return new FeedbackResponse(feedbackEntity);
    }

    @Override
    public void updateStatus(String feedbackId, String statusId) throws Exception {
        feedbackRepository.updateStatus(feedbackId, statusId);
    }

    @Override
    public List<FeedbackResponse> findByStatus(String statusId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByStatus(statusId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> findByEmail(String email, String eventId, String serviceId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByEmail(email, eventId, serviceId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> findByEventId(String eventId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByEventId(eventId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> findByServiceId(String serviceId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByServiceId(serviceId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> getReports() throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.getReports();
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

}
