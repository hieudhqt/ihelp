package com.swp.ihelp.app.eventcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCategoryService {
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    public EventCategoryService(EventCategoryRepository eventCategoryRepository) {
        this.eventCategoryRepository = eventCategoryRepository;
    }

    public List<EventCategoryEntity> findAll() throws Exception {
        return eventCategoryRepository.findAll();
    }

    public EventCategoryEntity findById(int id) throws Exception {
        Optional<EventCategoryEntity> result = eventCategoryRepository.findById(id);
        EventCategoryEntity eventCategory = null;
        if (result.isPresent()) {
            eventCategory = result.get();
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return eventCategory;
    }

    public void save(EventCategoryEntity eventCategoryEntity) throws Exception {
        eventCategoryRepository.save(eventCategoryEntity);
    }

    public void deleteById(int id) throws Exception {
        eventCategoryRepository.deleteById(id);
    }
}
