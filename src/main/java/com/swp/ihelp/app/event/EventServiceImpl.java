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
    public List<Event> findAll() throws Exception {
        return eventRepository.findAll();
    }

    @Override
    public Event findById(String id) throws Exception {
        Optional<Event> result = eventRepository.findById(id);
        Event event = null;
        if (result.isPresent()) {
            event = result.get();
        } else {
            throw new RuntimeException("Did not find event with id:" + id);
        }
        return event;
    }

    @Override
    public List<Event> findByTitle(String title) throws Exception {
        return eventRepository.findByTitle(title);
    }

    @Override
    public void save(Event event) throws Exception {
        // Set createDate as current date for new event.
        Optional<Event> result = eventRepository.findById(event.getId());
        if (result.isEmpty()) {
            event.setCreateDate(new Date());
        }
        eventRepository.save(event);
    }

    @Override
    public void deleteById(String id) throws Exception {
        eventRepository.deleteById(id);
    }
}
