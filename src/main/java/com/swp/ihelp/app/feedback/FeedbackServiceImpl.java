package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private FeedbackRepository feedbackRepository;

    private AccountRepository accountRepository;

    private EventRepository eventRepository;

    private ServiceRepository serviceRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, AccountRepository accountRepository, EventRepository eventRepository, ServiceRepository serviceRepository) {
        this.feedbackRepository = feedbackRepository;
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
        this.serviceRepository = serviceRepository;
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
    public List<FeedbackResponse> findByEmail(String email) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByEmail(email);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> findByEventId(String eventId, String statusId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByEventId(eventId, statusId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> findByServiceId(String serviceId, String statusId) throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findByServiceId(serviceId, statusId);
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public List<FeedbackResponse> getReports() throws Exception {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.getReports();
        return FeedbackResponse.convertToListResponse(feedbackEntities);
    }

    @Override
    public void approve(String feedbackId, String managerEmail) throws Exception {
        existsByFeedbackId(feedbackId);
        existsByEmail(managerEmail);
        FeedbackEntity feedbackEntity = feedbackRepository.getOne(feedbackId);
        if (feedbackEntity.getStatus().getId() != StatusEnum.PENDING.getId()) {
            throw new RuntimeException("You can only approve or reject feedback if it is pending");
        }
        if (feedbackEntity.getAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own feedback.");
        }
        AccountEntity approver = accountRepository.getOne(managerEmail);
        feedbackEntity.setManagerAccount(approver);
        feedbackEntity.setStatus(new StatusEntity().setId(StatusEnum.PENDING.getId()));
        feedbackRepository.save(feedbackEntity);
    }

    @Override
    public void reject(RejectFeedbackRequest request) throws Exception {
        String feedbackId = request.getFeedbackId();
        String managerEmail = request.getManagerEmail();
        existsByFeedbackId(feedbackId);
        existsByEmail(managerEmail);
        FeedbackEntity feedbackEntity = feedbackRepository.getOne(feedbackId);
        if (feedbackEntity.getStatus().getId() != StatusEnum.PENDING.getId()) {
            throw new RuntimeException("You can only approve or reject feedback if it is pending");
        }
        if (feedbackEntity.getAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own feedback.");
        }
        AccountEntity rejecter = accountRepository.getOne(managerEmail);
        feedbackEntity.setManagerAccount(rejecter);
        feedbackEntity.setStatus(new StatusEntity().setId(StatusEnum.REJECTED.getId()));
        feedbackEntity.setReason(request.getReason());
        feedbackRepository.save(feedbackEntity);
    }

    private void existsByEmail(String email) {
        if (!accountRepository.existsById(email)) {
            throw new RuntimeException("Account: " + email + " not found.");
        }
    }

    private void existsByFeedbackId(String feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Feedback with ID: " + feedbackId + " not found.");

        }
    }

    private void existsByEventId(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Event with ID: " + eventId + " not found.");
        }
    }

    private void existsByServiceId(String serviceId) {
        if (!serviceRepository.existsById(serviceId)) {
            throw new RuntimeException("Account: " + serviceId + " not found.");
        }
    }

}
