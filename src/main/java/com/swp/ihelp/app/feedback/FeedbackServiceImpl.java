package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private FeedbackRepository feedbackRepository;

    private AccountRepository accountRepository;

    private EventRepository eventRepository;

    private ServiceRepository serviceRepository;

    private ImageRepository imageRepository;

    @Value("${paging.page-size}")
    private int pageSize;


    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, AccountRepository accountRepository, EventRepository eventRepository, ServiceRepository serviceRepository, ImageRepository imageRepository) {
        this.feedbackRepository = feedbackRepository;
        this.accountRepository = accountRepository;
        this.eventRepository = eventRepository;
        this.serviceRepository = serviceRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public FeedbackEntity insert(FeedbackRequest request) throws Exception {
        FeedbackEntity feedbackEntity = FeedbackRequest.convertToEntity(request);
        return feedbackRepository.save(feedbackEntity);
    }

    @Override
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks = feedbackRepository.findAll(paging);
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMap(pageFeedbacks);
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
    public Map<String, Object> findByStatus(Integer statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks = feedbackRepository.findByStatus(statusId, paging);
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMap(pageFeedbacks);
    }

    @Override
    public Map<String, Object> findByEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks = feedbackRepository.findByEmail(email, paging);
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMapWithAvatar(pageFeedbacks);
    }

    @Override
    public Map<String, Object> findByEventId(String eventId, Integer statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks;
        if (statusId != null) {
            pageFeedbacks = feedbackRepository.findByEventIdWithStatusId(eventId, statusId, paging);
        } else {
            pageFeedbacks = feedbackRepository.findByEventId(eventId, paging);
        }
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMapWithAvatar(pageFeedbacks);
    }

    @Override
    public Map<String, Object> findByServiceId(String serviceId, Integer statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks;
        if (statusId != null) {
            pageFeedbacks = feedbackRepository.findByServiceIdWithStatusId(serviceId, statusId, paging);
        } else {
            pageFeedbacks = feedbackRepository.findByServiceId(serviceId, paging);
        }
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMapWithAvatar(pageFeedbacks);
    }

    @Override
    public Map<String, Object> getReports(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").descending());
        Page<FeedbackEntity> pageFeedbacks = feedbackRepository.getReports(paging);
        if (pageFeedbacks.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found.");
        }

        return getFeedbackResponseMap(pageFeedbacks);
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
        feedbackEntity.setStatus(new StatusEntity().setId(StatusEnum.APPROVED.getId()));
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

    private Map<String, Object> getFeedbackResponseMap(Page<FeedbackEntity> pageFeedbacks) throws Exception {
        List<FeedbackEntity> feedbackEntities = pageFeedbacks.getContent();
        List<FeedbackResponse> feedbackResponses = FeedbackResponse.convertToListResponse(feedbackEntities);

        Map<String, Object> response = new HashMap<>();
        response.put("feedbacks", feedbackResponses);
        response.put("currentPage", pageFeedbacks.getNumber());
        response.put("totalItems", pageFeedbacks.getTotalElements());
        response.put("totalPages", pageFeedbacks.getTotalPages());

        return response;
    }

    private Map<String, Object> getFeedbackResponseMapWithAvatar(Page<FeedbackEntity> pageFeedbacks) throws Exception {
        List<FeedbackEntity> feedbackEntities = pageFeedbacks.getContent();
        List<FeedbackResponse> feedbackResponses = FeedbackResponse.convertToListResponse(feedbackEntities);

        for (FeedbackResponse response : feedbackResponses) {
            response.setAvatarUrl(imageRepository.findAvatarByEmail(response.getEmail()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("feedbacks", feedbackResponses);
        response.put("currentPage", pageFeedbacks.getNumber());
        response.put("totalItems", pageFeedbacks.getTotalElements());
        response.put("totalPages", pageFeedbacks.getTotalPages());

        return response;
    }
}