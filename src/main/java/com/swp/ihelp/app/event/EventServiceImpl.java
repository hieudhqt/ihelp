package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.entity.SearchCriteria;
import com.swp.ihelp.app.event.request.CreateEventRequest;
import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.RejectEventRequest;
import com.swp.ihelp.app.event.request.UpdateEventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventDistanceResponse;
import com.swp.ihelp.app.event.response.EventHostedReport;
import com.swp.ihelp.app.event.response.EventResponse;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryRepository;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntityPK;
import com.swp.ihelp.app.eventjointable.EventHasAccountRepository;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.notification.DeviceRepository;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationRepository;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
import com.swp.ihelp.app.status.StatusRepository;
import com.swp.ihelp.exception.EntityNotFoundException;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import com.swp.ihelp.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@PropertySource("classpath:constant.properties")
public class EventServiceImpl implements EventService {

    @Value("${paging.page-size}")
    private int pageSize;

    @Value("${point.event.rating-2-bonus-percent}")
    private float rating2BonusPointPercent;

    @Value("${point.event.rating-3-bonus-percent}")
    private float rating3BonusPointPercent;

    @Value("${point.event.host-event-bonus}")
    private float hostBonusPointPercent;

    @Value("${date.event.minStartDateFromCreate}")
    private int minStartDateFromCreate;

    @Value("${date.startDateFromEndRegistration}")
    private int startDateFromEndRegistration;

    @Value("${pattern.search-filter}")
    private String filterPattern;

    private EventRepository eventRepository;
    private EventHasAccountRepository eventHasAccountRepository;
    private EventCategoryRepository categoryRepository;
    private ImageRepository imageRepository;
    private AccountRepository accountRepository;
    private PointRepository pointRepository;
    private StatusRepository statusRepository;
    private RewardRepository rewardRepository;

    //Use notification repository due to not using @Repository and @Service in the same class
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;

    //This service is only related to Firebase Cloud Messaging. It does not integrate with DAO layer or vice versa
    private final PushNotificationService pushNotificationService;

    private EventMessage eventMessage;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, EventHasAccountRepository eventHasAccountRepository, EventCategoryRepository categoryRepository, ImageRepository imageRepository, AccountRepository accountRepository, PointRepository pointRepository, StatusRepository statusRepository, RewardRepository rewardRepository, DeviceRepository deviceRepository, NotificationRepository notificationRepository, PushNotificationService pushNotificationService) {
        this.eventRepository = eventRepository;
        this.eventHasAccountRepository = eventHasAccountRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.accountRepository = accountRepository;
        this.pointRepository = pointRepository;
        this.statusRepository = statusRepository;
        this.rewardRepository = rewardRepository;
        this.deviceRepository = deviceRepository;
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("id").ascending()));
        Page<EventEntity> pageEvents = eventRepository.findAll(paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event not found.");
        }

        Map<String, Object> response = getEventResponseMap(pageEvents);
        return response;
    }

    @Override
    public Map<String, Object> findAll(int page, String search) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("id").ascending()));

        EventSpecificationBuilder builder = new EventSpecificationBuilder();
        Pattern pattern = Pattern.compile(filterPattern);
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            String orPredicate = null;
            if (matcher.end(3) <= search.length() - 1) {
                orPredicate = "" + search.charAt(matcher.end(3));
            }
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3), orPredicate);

        }
        Specification<EventEntity> spec = builder.build();
        Page<EventEntity> pageEvents = eventRepository.findAll(spec, paging);
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
            eventDetailResponse.setAvatarUrl(imageRepository.findAvatarByEmail(eventDetailResponse.getAccountEmail()));
        } else {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + id);
        }
        return eventDetailResponse;
    }

    @Override
    public Map<String, Object> findByTitle(String title, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("title").ascending()));
        EventSpecification spec = new EventSpecification(new SearchCriteria("title", ":", title));
        Page<EventEntity> pageEvents = eventRepository.findAll(spec, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with title: " + title + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findTitleById(String eventId) throws Exception {
        existsByEventId(eventId);
        String title = eventRepository.findTitleById(eventId);
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("title", title);
        return map;
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
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("title").ascending()));
        Page<EventEntity> pageEvents = eventRepository.findByAuthorEmail(email, paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event with author email: " + email + " not found.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> findByParticipantEmail(String email, Integer statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("event.startDate").descending().and(Sort.by("event.title").ascending()));
        Page<EventEntity> pageEvents;
        if (statusId != null) {
            pageEvents = eventRepository.findByParticipantEmailWithStatus(email, statusId, paging);
        } else {
            pageEvents = eventRepository.findByParticipantEmail(email, paging);
        }
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Account " + email + " has not joined any event.");
        }
        return getEventResponseMap(pageEvents);
    }

    @Override
    public boolean hasParticipants(String eventId) throws Exception {
        return eventRepository.hasParticipants(eventId);
    }

    @Override
    public Map<String, Object> findNearbyEvents(int page, float radius, double lat, double lng) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Object[]> pageEvents
                = eventRepository.getNearbyEvents(radius, lat, lng, StatusEnum.APPROVED.getId(), paging);

        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event not found.");
        }
        List<Object[]> listEvents = pageEvents.getContent();
        List<EventDistanceResponse> eventResponses = new ArrayList<>();
        for (Object[] obj : listEvents) {
            EventEntity eventEntity = eventRepository.getOne((String) obj[0]);
            EventDistanceResponse eventDistanceResponse = new EventDistanceResponse(eventEntity);
            eventDistanceResponse.setDistance((double) obj[1]);
            int spot = eventRepository.getRemainingSpot(eventEntity.getId());
            eventDistanceResponse.setSpot(spot);
            eventResponses.add(eventDistanceResponse);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("events", eventResponses);
        response.put("currentPage", pageEvents.getNumber());
        response.put("totalItems", pageEvents.getTotalElements());
        response.put("totalPages", pageEvents.getTotalPages());
        return response;
    }

    @Override
    public Map<Integer, Integer> getMonthlyHostedEventNumber(int year) throws Exception {
        List<EventHostedReport> monthlyReport = eventRepository.getMonthlyHostedEventNumber(year);
        if (monthlyReport == null) {
            throw new EntityNotFoundException("No record found.");
        }
        Map<Integer, Integer> recordMap = new HashMap<>(12);
        for (EventHostedReport eventHostedReport : monthlyReport) {
            recordMap.put(eventHostedReport.getMonth(), eventHostedReport.getCount());
        }

        Map<Integer, Integer> result = new HashMap<>(12);
        for (int i = 1; i <= 12; i++) {
            result.put(i, 0);
            if (recordMap.get(i) != null) {
                result.put(i, recordMap.get(i));
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> findByReferencedEventId(String eventId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<EventEntity> pageEvents
                = eventRepository.findByReferencedEventId(eventId, paging);

        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event not found.");
        }

        return getEventResponseMap(pageEvents);
    }

    @Override
    public Map<String, Object> getEventsWithInsufficientPoint(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("createdDate").ascending());
        Page<EventEntity> pageEvents
                = eventRepository.getEventsWithInsufficientPoint(paging);
        if (pageEvents.isEmpty()) {
            throw new EntityNotFoundException("Event not found.");
        }

        List<EventEntity> eventEntityList = pageEvents.getContent();
        List<EventResponse> eventResponses = convertEntitesToEventResponses(eventEntityList);

        for (EventResponse response : eventResponses) {
            int balancePoint = accountRepository.getBalancePoint(response.getAuthorEmail());
            int eventPoint = eventRepository.getPointById(response.getId());
            int quota = eventRepository.getQuota(response.getId());
            response.setPointShortage(Math.abs(balancePoint - (eventPoint * quota)));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("events", eventResponses);
        response.put("currentPage", pageEvents.getNumber());
        response.put("totalItems", pageEvents.getTotalElements());
        response.put("totalPages", pageEvents.getTotalPages());

        return response;
    }

    @Override
    public String insert(CreateEventRequest event) throws Exception {
        String errorMsg = validateCreateEvent(event);
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
        }

        EventEntity eventEntity = CreateEventRequest.convertToEntity(event);
        Set<ImageRequest> imageRequests = event.getImages();
        if (imageRequests != null) {
            for (ImageRequest imageRequest : imageRequests) {
                ImageEntity imageEntity = ImageRequest.convertRequestToEntity(imageRequest);
                imageEntity.setAuthorAccount(eventEntity.getAuthorAccount());
                eventEntity.addImage(imageEntity);
            }
        }
        if (event.getReferencedEventId() != null) {
            if (eventRepository.existsById(event.getReferencedEventId())) {
                EventEntity parentEvent = eventRepository.getOne(event.getReferencedEventId());
                eventEntity.setReferencedEvent(parentEvent);
            } else {
                throw new RuntimeException("Referenced event not existed.");
            }
        }

        EventEntity savedEvent = eventRepository.save(eventEntity);

        if (savedEvent.getStatus().getId() == StatusEnum.APPROVED.getId()) {
            AccountEntity hostAccount = accountRepository.getOne(eventEntity.getAuthorAccount().getEmail());

            int pointNeeded = savedEvent.getPoint() * savedEvent.getQuota();
            if (hostAccount.getBalancePoint() < pointNeeded) {
                throw new RuntimeException("Account " + hostAccount.getEmail() +
                        " does not have enough point");
            }

            hostAccount.decreaseBalancePoint(pointNeeded);
            accountRepository.save(hostAccount);

            PointEntity hostPointEntity = new PointEntity();
            hostPointEntity.setIsReceived(false);
            hostPointEntity.setDescription("Point used by " + hostAccount.getEmail() +
                    " to create event: " + savedEvent.getId());
            hostPointEntity.setEvent(savedEvent);
            hostPointEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            hostPointEntity.setAccount(hostAccount);
            hostPointEntity.setAmount(pointNeeded);
            pointRepository.save(hostPointEntity);
        }

        return savedEvent.getId();
    }

    @Override
    @Transactional
    public EventDetailResponse update(UpdateEventRequest eventRequest) throws Exception {
        String errorMsg = validateUpdateEvent(eventRequest);
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
        }

        EventEntity eventToUpdate = eventRepository.getOne(eventRequest.getId());

        eventToUpdate.setTitle(eventRequest.getTitle());
        eventToUpdate.setDescription(eventRequest.getDescription());
        eventToUpdate.setLocation(eventRequest.getLocation());
        eventToUpdate.setLatitude(eventRequest.getLatitude());
        eventToUpdate.setLongitude(eventRequest.getLongitude());
        eventToUpdate.setPoint(eventRequest.getPoint());
        eventToUpdate.setQuota(eventRequest.getQuota());
        eventToUpdate.setIsOnsite(eventRequest.getOnsite());
        eventToUpdate.setStartDate(new Timestamp(eventRequest.getStartDate().getTime()));
        eventToUpdate.setEndDate(new Timestamp(eventRequest.getEndDate().getTime()));
        eventToUpdate.setSuggestion(eventRequest.getSuggestion());

        if (eventRequest.getCategoryIds() != null) {
            Set<EventCategoryEntity> categoriesToUpdate = new HashSet<>();
            for (int categoryId : eventRequest.getCategoryIds()) {
                categoriesToUpdate.add(categoryRepository.findById(categoryId).get());
            }
            eventToUpdate.setEventCategories(categoriesToUpdate);
        }

        eventRepository.save(eventToUpdate);

        EventDetailResponse eventDetailResponse = new EventDetailResponse(eventToUpdate);
        int remainingSpot = eventRepository.getRemainingSpot(eventDetailResponse.getId());
        eventDetailResponse.setSpot(remainingSpot);
        eventDetailResponse.setAvatarUrl(imageRepository.findAvatarByEmail(eventDetailResponse.getAccountEmail()));

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

    @Override
    public EventEntity approve(String eventId, String managerEmail) throws Exception {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with ID:" + eventId + " not found.");
        }
        if (!accountRepository.existsById(managerEmail)) {
            throw new EntityNotFoundException("Account " + managerEmail + " not found.");
        }

        EventEntity eventEntity = eventRepository.getOne(eventId);

        if (eventEntity.getAuthorAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own event.");
        }

        AccountEntity authorAccount = eventEntity.getAuthorAccount();
        int pointNeeded = eventEntity.getPoint() * eventEntity.getQuota();
        if (authorAccount.getBalancePoint() < pointNeeded) {
            throw new RuntimeException("Account " + authorAccount.getEmail() +
                    " does not have enough point");
        }

        AccountEntity hostAccount = accountRepository.getOne(eventEntity.getAuthorAccount().getEmail());
        AccountEntity approver = accountRepository.getOne(managerEmail);

        eventEntity.setManagerAccount(approver);
        eventEntity.setStatus(new StatusEntity().setId(StatusEnum.APPROVED.getId()));
        EventEntity updatedEvent = eventRepository.save(eventEntity);

        hostAccount.decreaseBalancePoint(pointNeeded);
        accountRepository.save(hostAccount);

        PointEntity hostPointEntity = new PointEntity();
        hostPointEntity.setIsReceived(false);
        hostPointEntity.setDescription("Point used by " + hostAccount.getEmail() + " to create event: " + eventId);
        hostPointEntity.setEvent(new EventEntity().setId(eventId));
        hostPointEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        hostPointEntity.setAccount(hostAccount);
        hostPointEntity.setAmount(pointNeeded);
        pointRepository.save(hostPointEntity);

        return updatedEvent;
    }

    @Override
    public EventEntity reject(RejectEventRequest request) throws Exception {
        String eventId = request.getEventId();
        String managerEmail = request.getManagerEmail();
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with ID:" + eventId + " not found.");
        }
        if (!accountRepository.existsById(managerEmail)) {
            throw new EntityNotFoundException("Account " + managerEmail + " not found.");
        }

        EventEntity eventEntity = eventRepository.getOne(eventId);

        if (eventEntity.getStatus().getId() != StatusEnum.PENDING.getId()) {
            throw new RuntimeException("You can only approve or reject event if it is pending");
        }
        if (eventEntity.getAuthorAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own event.");
        }

        AccountEntity rejecter = accountRepository.getOne(managerEmail);

        eventEntity.setManagerAccount(rejecter);
        eventEntity.setStatus(new StatusEntity().setId(StatusEnum.REJECTED.getId()));
        eventEntity.setReason(request.getReason());
        EventEntity updatedEvent = eventRepository.save(eventEntity);
        return updatedEvent;
    }

    @Override
    @Transactional
    public void quitEvent(String eventId, String email) throws Exception {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found.");
        }
        if (!accountRepository.existsById(email)) {
            throw new EntityNotFoundException("Account not found.");
        }
        EventEntity event = eventRepository.getOne(eventId);
        if (event.getStatus().getId().equals(StatusEnum.APPROVED.getId())) {
            eventHasAccountRepository.quitEvent(eventId, email);
        } else {
            throw new RuntimeException("You can only quit event when it's status is Approved.");
        }
    }

    //1. Save feedback
    //2. Save PointEntity for both host and member
    //3. Update balance and cumulative point for host and member.
    @Override
    @Transactional
    public void evaluateMember(EvaluationRequest request) throws Exception {
        if (!eventRepository.existsById(request.getEventId())) {
            throw new EntityNotFoundException("Event not found.");
        }
        EventEntity eventEntity = eventRepository.getOne(request.getEventId());
        if (!eventEntity.getStatus().getName().equals("Completed")) {
            throw new RuntimeException("Event is not yet completed.");
        }
        AccountEntity memberAccount = accountRepository.getOne(request.getMemberEmail());

        EventHasAccountEntityPK eventHasAccountEntityPK = new
                EventHasAccountEntityPK(eventEntity.getId(), memberAccount.getEmail());

        if (!eventHasAccountRepository.existsById(eventHasAccountEntityPK)) {
            throw new RuntimeException("This account has not joined this event.");
        }
        if (eventHasAccountRepository.isMemberEvaluated(request.getEventId(),
                request.getMemberEmail())) {
            throw new RuntimeException("This account is already evaluated.");
        }
        EventHasAccountEntity eventHasAccount = eventHasAccountRepository
                .getOne(eventHasAccountEntityPK);
        eventHasAccount.setEvaluated(true);
        eventHasAccount.setRating(request.getRating().shortValue());
        eventHasAccountRepository.save(eventHasAccount);

        AccountEntity hostAccount = eventEntity.getAuthorAccount();

        int participationPoint = 0;
        int bonusPoint = 0;
        switch (request.getRating()) {
            case 1:
                participationPoint = 0;
                hostAccount.addBalancePoint(eventEntity.getPoint());
                accountRepository.save(hostAccount);
                break;
            case 2:
                participationPoint = eventEntity.getPoint();
                bonusPoint = Math.round(eventEntity.getPoint() * rating2BonusPointPercent);
                break;
            case 3:
                participationPoint = eventEntity.getPoint();
                bonusPoint = Math.round(eventEntity.getPoint() * rating3BonusPointPercent);
                break;
            default:
                throw new RuntimeException("Invalid rating.");
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PointEntity memberPointEntity = new PointEntity();
        memberPointEntity.setIsReceived(true);
        memberPointEntity.setDescription("Rating: " + request.getRating() + " \n" +
                "Description: " + request.getComment() + " \nEventID: " + request.getEventId());
        memberPointEntity.setEvent(eventEntity);
        memberPointEntity.setCreatedDate(currentTimestamp);
        memberPointEntity.setAccount(memberAccount);
        memberPointEntity.setAmount(participationPoint);
        pointRepository.save(memberPointEntity);

        memberAccount.addBalancePoint(participationPoint);
        if (bonusPoint > 0) {
            memberAccount.addContributionPoint(bonusPoint);
            RewardEntity reward = new RewardEntity();
            reward.setTitle("Reward for highly active in event: " + eventEntity.getId());
            reward.setDescription("");
            reward.setPoint(bonusPoint);
            reward.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            reward.setAccount(memberAccount);
            rewardRepository.save(reward);
        }

        accountRepository.save(memberAccount);
    }

    @Override
    @Transactional
    public List<String> findEvaluateRequiredByAuthorEmail(String email) throws Exception {
        return eventRepository.findEvaluateRequiredByAuthorEmail(email);
    }

    @Override
    public Boolean isUserHasEnoguhPoint(String email, String eventId) throws Exception {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found.");

        }
        if (!accountRepository.existsById(email)) {
            throw new EntityNotFoundException("Account not found.");
        }
        return accountRepository.getBalancePoint(email) >= (eventRepository.getPointById(eventId) * eventRepository.getQuota(eventId));
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

    @Override
    @Transactional
    public void disableEvent(String eventId) throws Exception {
        EventEntity eventEntity = eventRepository.getOne(eventId);
        if (eventEntity.getStatus().getId() != StatusEnum.APPROVED.getId() &&
                eventEntity.getStatus().getId() != StatusEnum.ONGOING.getId()) {
            throw new RuntimeException("Event can only be disabled when it's status is " +
                    "\"Approved\" or \"Ongoing\".");
        }
        eventRepository.updateStatus(eventId, StatusEnum.DISABLED.getId());

        int pointUsed = eventEntity.getPoint() * eventEntity.getQuota();
        if (pointUsed > 0) {
            accountRepository.updateBalancePoint(eventEntity.getAuthorAccount().getEmail(),
                    pointUsed);

            PointEntity hostPointEntity = new PointEntity();
            hostPointEntity.setIsReceived(true);
            hostPointEntity.setDescription("Point refunded to " + pointUsed + " for disabling event: " + eventId);
            hostPointEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            hostPointEntity.setEvent(eventEntity);
            hostPointEntity.setAccount(eventEntity.getAuthorAccount());
            hostPointEntity.setAmount(pointUsed);
            pointRepository.save(hostPointEntity);
        }
    }

    @Override
    @Transactional
    public String enableEvent(String eventId) throws Exception {
        EventEntity eventEntity = eventRepository.getOne(eventId);
        Date currentDate = new Date();
        if (eventEntity.getEndDate().before(currentDate)) {
            throw new RuntimeException("This event cannot be enabled because it has passed end date.");
        }
        if (!eventEntity.getStatus().getId().equals(StatusEnum.DISABLED.getId())) {
            throw new EntityNotFoundException("Events can only be enabled if it is \"Disabled\".");
        }
        int statusIdToUpdate = -1;
        if (eventEntity.getStartDate().after(currentDate)) {
            statusIdToUpdate = StatusEnum.APPROVED.getId();
        } else if (eventEntity.getStartDate().before(currentDate) &&
                eventEntity.getEndDate().after(currentDate)) {
            statusIdToUpdate = StatusEnum.ONGOING.getId();
        }
        if (statusIdToUpdate > 0) {
            int pointUsed = eventEntity.getPoint() * eventEntity.getQuota();
            if (pointUsed > 0) {
                if (eventEntity.getAuthorAccount().getBalancePoint() < pointUsed) {
                    throw new RuntimeException("Cannot enable this event due to author user does not have enough point.");
                }
                accountRepository.updateBalancePoint(eventEntity.getAuthorAccount().getEmail(),
                        -pointUsed);
                eventRepository.updateStatus(eventId, statusIdToUpdate);

                PointEntity hostPointEntity = new PointEntity();
                hostPointEntity.setIsReceived(false);
                hostPointEntity.setDescription("Point deducted for enabling event: " + eventId);
                hostPointEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                hostPointEntity.setEvent(eventEntity);
                hostPointEntity.setAccount(eventEntity.getAuthorAccount());
                hostPointEntity.setAmount(pointUsed);
                pointRepository.save(hostPointEntity);
            }
        } else {
            throw new RuntimeException("Invalid event status.");
        }
        return statusIdToUpdate == StatusEnum.APPROVED.getId() ? "Approved" : "Ongoing";
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

    @Override
    @Transactional
    public void completeEvent(String eventId) throws Exception {
        eventRepository.updateStatus(eventId, StatusEnum.COMPLETED.getId());
        EventEntity eventEntity = eventRepository.getOne(eventId);

        int contributionPoint = Math.round(eventEntity.getPoint() * hostBonusPointPercent);

        RewardEntity reward = new RewardEntity();
        reward.setTitle("Reward for hosting event: " + eventEntity.getId());
        reward.setDescription("");
        reward.setPoint(contributionPoint);
        reward.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        reward.setAccount(eventEntity.getAuthorAccount());
        rewardRepository.save(reward);

        String hostEmail = eventEntity.getAuthorAccount().getEmail();

        accountRepository.updateContributionPoint(hostEmail, contributionPoint);

        //Refund point when event participants number is lower than quota.
        int remainingSpots = eventRepository.getRemainingSpot(eventId);
        if (remainingSpots > 0) {
            int pointToRefund = eventEntity.getPoint() * remainingSpots;
            accountRepository.updateBalancePoint(hostEmail, pointToRefund);

            PointEntity hostPointEntity = new PointEntity();
            hostPointEntity.setIsReceived(true);
            hostPointEntity.setDescription("Point refunded to " + hostEmail + " due to event: " + eventId + " members count did not reach quota.");
            hostPointEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            hostPointEntity.setAccount(eventEntity.getAuthorAccount());
            hostPointEntity.setAmount(pointToRefund);
            hostPointEntity.setEvent(eventEntity);
            pointRepository.save(hostPointEntity);
        }

        //Push notification to event's participant
        PushNotificationRequest participantsNotificationRequest = new PushNotificationRequest()
                .setTitle("Event \"" + eventEntity.getTitle() + "\" is completed")
                .setMessage("Event \"" + eventEntity.getTitle() + "\" is now being under host's evaluation")
                .setTopic(eventId);

        pushNotificationService.sendPushNotificationToTopic(participantsNotificationRequest);

        //Push notification to event's host
        List<String> hostDeviceTokens = deviceRepository.findByEmail(eventEntity.getAuthorAccount().getEmail());

        if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("evaluateRequiredEvents", eventId);

            PushNotificationRequest hostNotificationRequest = new PushNotificationRequest()
                    .setTitle("Your event: \"" + eventEntity.getTitle() + "\" is completed")
                    .setMessage("Event \"" + eventEntity.getTitle() + "\" has participants in need of evaluating")
                    .setData(notificationData)
                    .setRegistrationTokens(hostDeviceTokens);

            pushNotificationService.sendPushNotificationToMultiDevices(hostNotificationRequest);
        }

        notificationRepository.save(new NotificationEntity()
                .setTitle("Your event: \"" + eventEntity.getTitle() + "\" is completed")
                .setMessage("Event \"" + eventEntity.getTitle() + "\" has participants in need of evaluating")
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(eventEntity.getAuthorAccount().getEmail())));
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

        if (eventRepository.isAccountJoinedAnyEvent(event.getStartDate(), event.getEndDate(), email) != null) {
            errorMsg += "This email has already joined another event;";
        }

        Calendar endRegistrationDate = Calendar.getInstance();
        endRegistrationDate.setTime(event.getStartDate());
        endRegistrationDate.add(Calendar.DAY_OF_MONTH, -startDateFromEndRegistration);
        endRegistrationDate.set(Calendar.HOUR_OF_DAY, 0);
        endRegistrationDate.set(Calendar.MINUTE, 0);
        endRegistrationDate.set(Calendar.SECOND, 0);
        endRegistrationDate.set(Calendar.MILLISECOND, 0);

        Calendar currentCalendar = Calendar.getInstance();
        if (currentCalendar.compareTo(endRegistrationDate) > 0) {
            errorMsg += "Registration deadline is due.";
        }

        return errorMsg;
    }

    private String validateCreateEvent(CreateEventRequest request) {
        String errorMsg = "";

        long diffInMillies = Math.abs(request.getStartDate().getTime() - System.currentTimeMillis());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diffInDays < minStartDateFromCreate) {
            errorMsg += "Start date must be at least " + minStartDateFromCreate +
                    " days after creation date; ";
        }

        if (request.getStartDate().after(request.getEndDate())) {
            errorMsg += "Start date cannot be sooner than end date.";
        }

        return errorMsg;
    }

    private String validateUpdateEvent(UpdateEventRequest request) {
        String errorMsg = "";

        if (!eventRepository.existsById(request.getId())) {
            throw new EntityNotFoundException("Event with ID:" + request.getId() + " not found.");
        }
        if (request.getEndDate().getTime()
                <= request.getStartDate().getTime()) {
            throw new RuntimeException("End date must be later than start date.");
        }

        EventEntity eventToUpdate = eventRepository.getOne(request.getId());

        long diffInMillies = Math.abs(request.getStartDate().getTime()
                - eventToUpdate.getCreatedDate().getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diffInDays < minStartDateFromCreate) {
            errorMsg += "Start date must be at least " + minStartDateFromCreate +
                    " days after creation date; ";
        }

        if (request.getStartDate().after(request.getEndDate())) {
            errorMsg += "Start date cannot be sooner than end date.";
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

    private void existsByEventId(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("Event with ID: " + eventId + " not found.");
        }
    }
}
