package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.request.EventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventResponse;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private AccountRepository accountRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, AccountRepository accountRepository) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<EventResponse> findAll() throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findAll();
        return getEventResponses(eventEntityList);
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
            throw new EntityNotFoundException("Did not find event with id:" + id);
        }
        return event;
    }

    @Override
    public List<EventResponse> findByTitle(String title) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByTitle(title);
        return getEventResponses(eventEntityList);
    }

    @Override
    public void save(EventRequest event) throws Exception {
        EventEntity eventEntity = EventRequest.convertToEntity(event);
        List<ImageEntity> imageEntities = event.getImages();
        if (imageEntities != null || !imageEntities.isEmpty()) {
            for (ImageEntity imageEntity : imageEntities) {
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
            throw new EntityNotFoundException("Event id not found:" + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponse> findByCategoryId(int categoryId) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByCategoryId(categoryId);
        return getEventResponses(eventEntityList);
    }

    @Override
    public List<EventResponse> findByStatusId(int statusId) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByStatusId(statusId);
        return getEventResponses(eventEntityList);
    }

    @Override
    public List<EventResponse> findByAuthorEmail(String email) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByAuthorEmail(email);
        return getEventResponses(eventEntityList);
    }

    @Override
    public void joinEvent(String email, String eventId) throws Exception {
        Optional<EventEntity> eventEntityOptional = eventRepository.findById(eventId);
        EventEntity eventEntity = eventEntityOptional.get();

        if (!isEventAvailable(eventEntity, System.currentTimeMillis())) {
            return;
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

    private List<EventDetailResponse> convertToEventDetailResponses(List<EventEntity> eventEntityList)
            throws Exception {
        if (eventEntityList.isEmpty()) {
            throw new EntityNotFoundException("No event found.");
        }
        return EventDetailResponse.convertToResponseList(eventEntityList);
    }

    private List<EventDetailResponse> getEventDetailResponses(List<EventEntity> eventEntityList) throws Exception {
        List<EventDetailResponse> result = convertToEventDetailResponses(eventEntityList);
        for (EventDetailResponse response : result) {
            int remainingSpot = eventRepository.getRemainingSpots(response.getId());
            int quota = response.getQuota();
            if (quota >= remainingSpot) {
                response.setSpot(quota - remainingSpot);
            } else {
                response.setSpot(0);
            }
        }
        return result;
    }

    private List<EventResponse> getEventResponses(List<EventEntity> eventEntityList) throws Exception {
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
