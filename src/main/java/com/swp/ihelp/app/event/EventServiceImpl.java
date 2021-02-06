package com.swp.ihelp.app.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
            throw new RuntimeException("Did not find event with id:" + id);
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
//        if (event.getId()!=null) {
//            Optional<EventEntity> result = eventRepository.findById(event.getId());
//            if (result.isEmpty()) {
//                event.setCreatedDate(new Date().getTime());
//            }
//        }
        eventRepository.save(event);
    }

    @Override
    public void deleteById(String id) throws Exception {
        eventRepository.deleteById(id);
    }
}
