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
    private long minStartDateFromCreate;

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

        Map<String, Object> response = getEventResponseMap(pageEvents);
        return response;
    }

    @Override
    public EventDetailResponse findById(String id) throws Exception {
        Optional<EventEntity> result = eventRepository.findById(id);
        EventDetailResponse event = null;
        if (result.isPresent()) {
            event = new EventDetailResponse(result.get());
            int remainingSpot = eventRepository.getSpotUsed(id);
            int quota = eventRepository.getQuota(id);
            if (quota >= remainingSpot) {
                event.setSpot(quota - remainingSpot);
            } else {
                event.setSpot(0);
            }
        } else {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + id);
        }
        return event;
    }

    @Override
    public Map<String, Object> findByTitle(String title, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByTitle(title, paging);

        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByCategoryId(int categoryId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByCategoryId(categoryId, paging);

        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByStatusId(int statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByStatusId(statusId, paging);

        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByAuthorEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByAuthorEmail(email, paging);

        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByParticipantEmail(String email, int statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents = eventRepository.findByParticipantEmail(email, statusId, paging);

        return getEventResponseMap(pageEvents);
    }

    @Override
    public void insert(CreateEventRequest event) throws Exception {
        String errorMsg = validateCreateEvent(event);
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
        }

        EventEntity eventEntity = CreateEventRequest.convertToEntity(event);
        List<ImageRequest> imageRequests = event.getImages();
        System.out.println("size:" + eventEntity.getEventCategories().size());
        if (imageRequests != null) {
            for (ImageRequest imageRequest : imageRequests) {
                ImageEntity imageEntity = ImageRequest.convertRequestToEntity(imageRequest);
                imageEntity.setAuthorAccount(eventEntity.getAuthorAccount());
                ImageEntity savedImage = imageRepository.save(imageEntity);
                eventEntity.addImage(savedImage);
            }
        }
        eventRepository.save(eventEntity);
    }

    @Override
    @Transactional
    public void update(UpdateEventRequest event) throws Exception {
        EventEntity eventEntity = UpdateEventRequest.convertToEntity(event);
        eventRepository.update(eventEntity);
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

            int participationPoint = (int) Math.floor((request.getRating() / 5) * eventEntity.getPoint());

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            PointEntity hostPointEntity = new PointEntity();
            hostPointEntity.setIsReceived(false);
            hostPointEntity.setDescription("Rating: " + request.getRating() + " stars\n" +
                    "Description: " + request.getComment());
            hostPointEntity.setCreatedDate(currentTimestamp);
            hostPointEntity.setAccount(hostAccount);
            hostPointEntity.setEvent(eventEntity);
            hostPointEntity.setAmount(participationPoint);
            pointRepository.save(hostPointEntity);

            PointEntity memberPointEntity = new PointEntity();
            memberPointEntity.setIsReceived(true);
            memberPointEntity.setDescription("Rating: " + request.getRating() + " stars\n" +
                    "Description: " + request.getComment());
            memberPointEntity.setCreatedDate(currentTimestamp);
            memberPointEntity.setAccount(memberAccount);
            memberPointEntity.setEvent(eventEntity);
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
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + eventId);
        }

        EventEntity eventEntity = eventRepository.getOne(eventId);

        if (email.equals(eventEntity.getAuthorAccount().getEmail())) {
            throw new RuntimeException("Event host cannot join his/her own event.");
        }

        if (!isEventAvailable(eventEntity, System.currentTimeMillis())) {
            throw new RuntimeException(eventMessage.getEventUnavailableMessage());
        }

        EventHasAccountEntity eventAccount = new EventHasAccountEntity();
        eventAccount.setEvent(new EventEntity().setId(eventId));
        eventAccount.setAccount(new AccountEntity().setEmail(email));
        eventEntity.addEventAccount(eventAccount);

        eventRepository.save(eventEntity);
    }

    private boolean isEventAvailable(EventEntity event, long currentDateInMillis) throws Exception {
        boolean check = true;
        if (!event.getStatus().getName().equals("Approved")) {
            check = false;
            System.out.println("Event is not approved.");
        }
        int spotUsed = eventRepository.getSpotUsed(event.getId());
        if (spotUsed == event.getQuota()) {
            check = false;
            System.out.println("Event is full.");
        }
        return check;
    }


//    private List<EventDetailResponse> convertToEventDetailResponses(List<EventEntity> eventEntityList)
//            throws Exception {
//        if (eventEntityList.isEmpty()) {
//            throw new EntityNotFoundException("No event found.");
//        }
//        return EventDetailResponse.convertToResponseList(eventEntityList);
//    }

//    private List<EventDetailResponse> getEventDetailResponses(List<EventEntity> eventEntityList) throws Exception {
//        List<EventDetailResponse> result = convertToEventDetailResponses(eventEntityList);
//        for (EventDetailResponse response : result) {
//            int remainingSpot = eventRepository.getRemainingSpots(response.getId());
//            int quota = response.getQuota();
//            if (quota >= remainingSpot) {
//                response.setSpot(quota - remainingSpot);
//            } else {
//                response.setSpot(0);
//            }
//        }
//        return result;
//    }

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
            int remainingSpot = eventRepository.getSpotUsed(response.getId());
            int quota = eventRepository.getQuota(response.getId());
            if (quota >= remainingSpot) {
                response.setSpot(quota - remainingSpot);
            } else {
                response.setSpot(0);
            }
//            int remainingSpot = eventRepository.getRemainingSpot(response.getId());
        }
        return result;
    }

    private String validateCreateEvent(CreateEventRequest event) {
        String errorMsg = "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDateString = dateFormat.format(event.getStartDate());
        String endDateString = dateFormat.format(event.getEndDate());

        Date nearestStartDate = eventRepository.getNearestEventStartDate(event.getAuthorEmail(),
                startDateString);
        Date nearestEndDate = eventRepository.getNearestEventEndDate(event.getAuthorEmail(),
                endDateString);

        if (nearestStartDate != null) {
            if (nearestStartDate.before(event.getEndDate())) {
                errorMsg += "You have an event that starts on the same date as your end date; ";
            }
        }
        if (nearestEndDate != null) {
            if ((event.getStartDate().getTime() - nearestEndDate.getTime()) <= minStartDateFromNearestEndDate) {
                errorMsg += "Start date must be at least 1 day after the previous event ended; ";
            }
        }

        if (event.getStartDate().getTime() - System.currentTimeMillis() < minStartDateFromCreate) {
            errorMsg += "Start date must be at least 3 days after creation date; ";
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
