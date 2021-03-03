package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.request.EventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventResponse;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.exception.EntityNotFoundException;
import com.swp.ihelp.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@PropertySource("classpath:constant.properties")
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private EventMessage eventMessage;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
            int remainingSpot = eventRepository.getRemainingSpots(id);
            int quota = event.getQuota();
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
    public void save(EventRequest event) throws Exception {
        EventEntity eventEntity = EventRequest.convertToEntity(event);
        List<ImageRequest> imageRequests = event.getImages();
        if (imageRequests != null || !imageRequests.isEmpty()) {
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
    public void deleteById(String id) throws Exception {
        Optional<EventEntity> event = eventRepository.findById(id);
        if (!event.isPresent()) {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public void joinEvent(String email, String eventId) throws Exception {
        Optional<EventEntity> eventEntityOptional = eventRepository.findById(eventId);
        if (!eventEntityOptional.isPresent()) {
            throw new EntityNotFoundException(eventMessage.getEventNotFoundMessage() + eventId);
        }
        EventEntity eventEntity = eventEntityOptional.get();

        if (!isEventAvailable(eventEntity, System.currentTimeMillis())) {
            throw new RuntimeException(eventMessage.getEventUnavailableMessage());
        }

        EventHasAccountEntity eventAccount = new EventHasAccountEntity();
        eventAccount.setEvent(eventEntity);
        eventAccount.setAccount(new AccountEntity().setEmail(email));

        eventEntity.getEventAccount().add(eventAccount);

        EventEntity savedEvent = eventRepository.save(eventEntity);
    }

    private boolean isEventAvailable(EventEntity event, long currentDateInMillis) throws Exception {
        boolean check = true;
        if (!event.getStatus().getName().equals("Approved")) {
            check = false;
        }
        int remainingSpots = eventRepository.getRemainingSpots(event.getId());
        if (remainingSpots < 1) {
            check = false;
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
            int remainingSpot = eventRepository.getRemainingSpots(response.getId());
            int quota = eventRepository.getQuota(response.getId());
            if (quota >= remainingSpot) {
                response.setSpot(quota - remainingSpot);
            } else {
                response.setSpot(0);
            }
        }
        return result;
    }
}
