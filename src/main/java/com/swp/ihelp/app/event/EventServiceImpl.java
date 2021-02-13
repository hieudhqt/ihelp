package com.swp.ihelp.app.event;

import com.swp.ihelp.app.entity.AccountEntity;
import com.swp.ihelp.app.entity.AccountRepository;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private AccountRepository accountRepository;
    private EntityManager entityManager;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, AccountRepository accountRepository, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<EventEntity> findAll() throws Exception {
        return eventRepository.findAll();
    }

    @Override
    public EventEntity findById(String id) throws Exception {
        Optional<EventEntity> result = eventRepository.findById(id);
        EventEntity event = null;
        if (result.isPresent()) {
            event = result.get();
        } else {
            throw new EntityNotFoundException("Did not find event with id:" + id);
        }
        return event;
    }

    @Override
    public List<EventEntity> findByTitle(String title) throws Exception {
        return eventRepository.findByTitle(title);
    }

    @Override
    public void save(EventEntity event) throws Exception {
        // Set createDate as current date for new event.
        if (event.getId() != null) {
            Optional<EventEntity> result = eventRepository.findById(event.getId());
            if (result.isEmpty()) {
                event.setCreatedDate(new Date().getTime());
            }
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
    public List<EventEntity> findByCategoryId(int categoryId) throws Exception {
        List<EventEntity> result = eventRepository.findByCategoryId(categoryId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Event with category id:" + categoryId + "not found.");
        }
        return result;
    }

    @Override
    public List<EventEntity> findByStatusId(int statusId) throws Exception {
        List<EventEntity> result = eventRepository.findByStatusId(statusId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Event with status id:" + statusId + "not found.");
        }
        return result;
    }

    @Override
    public List<EventEntity> findByAuthorEmail(String email) throws Exception {
        return eventRepository.findByAuthorEmail(email);
    }

    @Override
    public void joinEvent(String email, String eventId) throws Exception{
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
}
