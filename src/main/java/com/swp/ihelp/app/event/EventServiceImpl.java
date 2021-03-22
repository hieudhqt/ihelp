package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.request.CreateEventRequest;
import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.UpdateEventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventResponse;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.eventjointable.EventHasAccountRepository;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
import com.swp.ihelp.app.status.StatusRepository;
import com.swp.ihelp.exception.EntityNotFoundException;
import com.swp.ihelp.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@PropertySource("classpath:constant.properties")
public class EventServiceImpl implements EventService {

    @Value("${paging.page-size}")
    private int pageSize;

    @Value("${date.minStartDateFromNearestEndDate}")
    private long minStartDateFromNearestEndDate;

    @Value("${date.minStartDateFromCreate}")
    private int minStartDateFromCreate;

    @Value("${date.startDateFromEndRegistration}")
    private int startDateFromEndRegistration;

    private EventRepository eventRepository;
    private EventHasAccountRepository eventHasAccountRepository;
    private ImageRepository imageRepository;
    private EventMessage eventMessage;
    private AccountRepository accountRepository;
    private PointRepository pointRepository;
    private StatusRepository statusRepository;

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Autowired
    public void setEventHasAccountRepository(EventHasAccountRepository eventHasAccountRepository) {
        this.eventHasAccountRepository = eventHasAccountRepository;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Autowired
    public void setEventMessage(EventMessage eventMessage) {
        this.eventMessage = eventMessage;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setPointRepository(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Autowired
    public void setStatusRepository(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findAll(paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event not found.");
        }

        Map<String, Object> response = getEventResponseMap(pageEvents);
        return response;
    }

    @Override
    public EventDetailResponse findById(String id) throws Exception {
        Optional<EventEntity> result = eventRepository.findById(id);
        EventDetailResponse eventDetailResponse = null;
        if (result.isPresent()) {
            eventDetailResponse = new EventDetailResponse(result.get());
            int remainingSpot = eventRepository.getRemainingSpot(id);
            eventDetailResponse.setSpot(remainingSpot);
        } else {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + id);
        }
        return eventDetailResponse;
    }

    @Override
    public Map<String, Object> findByTitle(String title, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByTitle(title, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with title: " + title + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByCategoryId(int categoryId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByCategoryId(categoryId, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with category ID: " + categoryId + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByStatusId(int statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByStatusId(statusId, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with status ID: " + statusId + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByAuthorEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByAuthorEmail(email, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with author email: " + email + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByParticipantEmail(String email, int statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByParticipantEmail(email, statusId, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Account " + email + " has not joined any event.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public String insert(CreateEventRequest event) throws Exception {
        String errorMsg = validateCreateEvent(event);
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
        }

        EventEntity eventEntity = CreateEventRequest.convertToEntity(event);
        List<ImageRequest> imageRequests = event.getImages();
        if (imageRequests != null) {
            for (ImageRequest imageRequest : imageRequests) {
                ImageEntity imageEntity = ImageRequest.convertRequestToEntity(imageRequest);
                imageEntity.setAuthorAccount(eventEntity.getAuthorAccount());
                ImageEntity savedImage = imageRepository.save(imageEntity);
                eventEntity.addImage(savedImage);
            }
        }
        EventEntity savedEvent = eventRepository.save(eventEntity);
        return savedEvent.getId();
    }

    @Override
    @Transactional
    public EventDetailResponse update(UpdateEventRequest eventRequest) throws Exception {
        if (!eventRepository.existsById(eventRequest.getId())) {
            throw new EntityNotFoundException("Event with ID:" + eventRequest.getId() + " not found.");
        }
        EventEntity eventToUpdate = eventRepository.getOne(eventRequest.getId());

        eventToUpdate.setTitle(eventRequest.getTitle());
        eventToUpdate.setDescription(eventRequest.getDescription());
        eventToUpdate.setLocation(eventRequest.getLocation());
        eventToUpdate.setPoint(eventRequest.getPoint());
        eventToUpdate.setQuota(eventRequest.getQuota());
        eventToUpdate.setOnsite(eventRequest.isOnsite());
        eventToUpdate.setStartDate(new Timestamp(eventRequest.getStartDate().getTime()));
        eventToUpdate.setEndDate(new Timestamp(eventRequest.getEndDate().getTime()));

        eventRepository.save(eventToUpdate);

        EventDetailResponse eventDetailResponse = new EventDetailResponse(eventToUpdate);
        int remainingSpot = eventRepository.getRemainingSpot(eventDetailResponse.getId());
        eventDetailResponse.setSpot(remainingSpot);

        return eventDetailResponse;
    }

    @Override
    @Transactional
    public void updateStatus(String eventId, int statusId) throws Exception {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with ID:" + eventId + " not found.");
        }
        if (!statusRepository.existsById(statusId)) {
            throw new EntityNotFoundException("Status with ID:" + statusId + " not found.");
        }

        eventRepository.updateStatus(eventId, statusId);
    }

    //1. Save feedback
    //2. Save PointEntity for both host and member
    //3. Update balance and cumulative point for host and member.
    @Override
    @Transactional
    public void evaluateMember(EvaluationRequest request) throws Exception {
        EventEntity eventEntity = eventRepository.getOne(request.getEventId());
        if (!eventEntity.getStatus().getName().equals("Completed")) {
            throw new RuntimeException("Event is not yet completed.");
        }
        AccountEntity memberAccount = accountRepository.getOne(request.getMemberEmail());
        boolean valid = false;
        List<String> memberEmails = eventHasAccountRepository.findMemberEmailsByEventId(request.getEventId());
        for (String email : memberEmails) {
            if (email.equals(memberAccount.getEmail())) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            throw new RuntimeException("Member account is invalid!");
        } else {
            AccountEntity hostAccount = eventEntity.getAuthorAccount();

            int participationPoint = (int) Math.floor((request.getRating() / 5f) * eventEntity.getPoint());

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            PointEntity hostPointEntity = new PointEntity();
            hostPointEntity.setIsReceived(false);
            hostPointEntity.setDescription("Rating: " + request.getRating() + " stars\n" +
                    "Description: " + request.getComment());
            hostPointEntity.setCreatedDate(currentTimestamp);
            hostPointEntity.setAccount(hostAccount);
            hostPointEntity.setAmount(participationPoint);
            pointRepository.save(hostPointEntity);

            PointEntity memberPointEntity = new PointEntity();
            memberPointEntity.setIsReceived(true);
            memberPointEntity.setDescription("Rating: " + request.getRating() + " stars\n" +
                    "Description: " + request.getComment());
            memberPointEntity.setCreatedDate(currentTimestamp);
            memberPointEntity.setAccount(memberAccount);
            memberPointEntity.setAmount(participationPoint);
            pointRepository.save(memberPointEntity);

            memberAccount.addBalancePoint(participationPoint);
            memberAccount.addContributionPoint(participationPoint);
            hostAccount.decreaseBalancePoint(participationPoint);
            hostAccount.addContributionPoint(participationPoint);
            accountRepository.save(memberAccount);
            accountRepository.save(hostAccount);
        }
    }

    @Override
    public void deleteById(String id) throws Exception {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + id);
        }
        EventEntity eventEntity = eventRepository.getOne(id);
        if (!eventEntity.getStatus().getName().equals("Pending") ||
                !eventEntity.getEventAccount().isEmpty()) {
            throw new RuntimeException("Event can only be deleted if it is in pending state.");
        }
        eventRepository.deleteById(id);
    }

    //1. Get EventEntity from eventId
    //2. Check if event is available to join
    //3. Save event and member in database
    @Override
    public void joinEvent(String email, String eventId) throws Exception {
        String errorMsg = validateJoinEvent(eventId, email);
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
        }

        EventEntity eventEntity = eventRepository.getOne(eventId);

        EventHasAccountEntity eventAccount = new EventHasAccountEntity();
        eventAccount.setEvent(new EventEntity().setId(eventId));
        eventAccount.setAccount(new AccountEntity().setEmail(email));
        eventEntity.addEventAccount(eventAccount);

        eventRepository.save(eventEntity);
    }

    private String validateJoinEvent(String eventId, String email) throws Exception {
        String errorMsg = "";
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + eventId);
        }

        EventEntity event = eventRepository.getOne(eventId);
        if (!event.getStatus().getName().equals("Approved")) {
            errorMsg += "Event is not approved;";
        }
        int spotUsed = eventRepository.getSpotUsed(event.getId());
        if (spotUsed == event.getQuota()) {
            errorMsg += "Event is full;";
        }
        if (email.equals(event.getAuthorAccount().getEmail())) {
            errorMsg += "Event host cannot join his/her own event;";
        }

        Calendar endRegistrationDate = Calendar.getInstance();
        endRegistrationDate.setTime(event.getStartDate());
        endRegistrationDate.add(Calendar.DAY_OF_MONTH, -startDateFromEndRegistration);
        endRegistrationDate.set(Calendar.HOUR_OF_DAY, 0);
        endRegistrationDate.set(Calendar.MINUTE, 0);
        endRegistrationDate.set(Calendar.SECOND, 0);
        endRegistrationDate.set(Calendar.MILLISECOND, 0);

        Calendar currentDate = Calendar.getInstance();
        if (currentDate.compareTo(endRegistrationDate) > 0) {
            errorMsg += "Registration deadline is due.";
        }

        return errorMsg;
    }

    private Map<String, Object> getEventResponseMap(Page<EventEntity> pageEvents) throws Exception {
        List<EventEntity> eventEntityList = pageEvents.getContent();
        List<EventResponse> eventResponses = convertEntitesToEventResponses(eventEntityList);

        Map<String, Object> response = new HashMap<>();
        response.put("events", eventResponses);
        response.put("currentPage", pageEvents.getNumber());
        response.put("totalItems", pageEvents.getTotalElements());
        response.put("totalPages", pageEvents.getTotalPages());

        return response;
    }

    private List<EventResponse> convertEntitesToEventResponses(List<EventEntity> eventEntityList) throws Exception {
        List<EventResponse> result = EventResponse.convertToResponseList(eventEntityList);
        for (EventResponse response : result) {
            int remainingSpot = eventRepository.getRemainingSpot(response.getId());
            response.setSpot(remainingSpot);
        }
        return result;
    }

    private String validateCreateEvent(CreateEventRequest event) {
        String errorMsg = "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        String startDateString = dateFormat.format(event.getStartDate());
//        String endDateString = dateFormat.format(event.getEndDate());
//
//        Date nearestStartDate = eventRepository.getNearestEventStartDate(event.getAuthorEmail(),
//                startDateString);
//        Date nearestEndDate = eventRepository.getNearestEventEndDate(event.getAuthorEmail(),
//                endDateString);
//
//        if (nearestStartDate != null) {
//            if (nearestStartDate.before(event.getEndDate())) {
//                errorMsg += "You have an event that starts on the same date as your end date; ";
//            }
//        }
//        if (nearestEndDate != null) {
//            if ((event.getStartDate().getTime() - nearestEndDate.getTime()) <= minStartDateFromNearestEndDate) {
//                errorMsg += "Start date must be at least 1 day after the previous event ended; ";
//            }
//        }

        if (event.getStartDate().getTime() - System.currentTimeMillis() < minStartDateFromCreate) {
            errorMsg += "Start date must be at least " + minStartDateFromCreate +
                    " days after creation date; ";
        }

        String authorEmail = event.getAuthorEmail();
        if (accountRepository.getOne(authorEmail).getBalancePoint() < event.getPoint()) {
            errorMsg += "Account " + authorEmail + " does not have enough point; ";
        }
        if (event.getStartDate().after(event.getEndDate())) {
            errorMsg += "Start date cannot be sooner than end date.";
        }

        return errorMsg;
    }
}
