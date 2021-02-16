package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private AccountRepository accountRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, AccountRepository accountRepository) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<EventResponse> findAll() throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findAll();
        List<EventResponse> responseList = convertToResponseObject(eventEntityList);
        return responseList;
    }

    @Override
    public EventResponse findById(String id) throws Exception {
        Optional<EventEntity> result = eventRepository.findById(id);
        EventResponse event = null;
        if (result.isPresent()) {
            event = new EventResponse(result.get());
        } else {
            throw new EntityNotFoundException("Did not find event with id:" + id);
        }
        return event;
    }

    @Override
    public List<EventResponse> findByTitle(String title) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByTitle(title);
        List<EventResponse> responseList = convertToResponseObject(eventEntityList);
        return responseList;
    }

    @Override
    public void save(EventEntity event) throws Exception {
        // Set createDate as current date for new event.
        if (event.getId() == null) {
            event.setCreatedDate(new Date().getTime());
        }
        eventRepository.save(event);
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
        List<EventResponse> responseList = convertToResponseObject(eventEntityList);
        return responseList;
    }

    @Override
    public List<EventResponse> findByStatusId(int statusId) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByStatusId(statusId);
        List<EventResponse> responseList = convertToResponseObject(eventEntityList);
        return responseList;
    }

    @Override
    public List<EventResponse> findByAuthorEmail(String email) throws Exception {
        List<EventEntity> eventEntityList = eventRepository.findByAuthorEmail(email);
        List<EventResponse> responseList = convertToResponseObject(eventEntityList);
        return responseList;
    }

    @Override
    public void joinEvent(String email, String eventId) throws Exception {
        Optional<EventEntity> eventEntityOptional = eventRepository.findById(eventId);
        EventEntity eventEntity = eventEntityOptional.get();

        Optional<AccountEntity> accountEntityOptional = accountRepository.findById(email);
        AccountEntity accountEntity = accountEntityOptional.get();

        EventHasAccountEntity eventAccount = new EventHasAccountEntity();
        eventAccount.setEvent(eventEntity);
        eventAccount.setAccount(accountEntity);

        eventEntity.getEventAccount().add(eventAccount);

        EventEntity savedEvent = eventRepository.save(eventEntity);
//        AccountEntity savedAccount = accountRepository.save(accountEntity);
    }

    private List<EventResponse> convertToResponseObject(List<EventEntity> eventEntityList)
            throws Exception {
        if (eventEntityList.isEmpty()) {
            throw new EntityNotFoundException("No event found.");
        }
        return EventResponse.convertToResponseList(eventEntityList);
    }
}
