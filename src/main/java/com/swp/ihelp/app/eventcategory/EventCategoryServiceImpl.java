package com.swp.ihelp.app.eventcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCategoryServiceImpl implements EventCategoryService {
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    public EventCategoryServiceImpl(EventCategoryRepository eventCategoryRepository) {
        this.eventCategoryRepository = eventCategoryRepository;
    }

    @Override
    public List<EventCategoryEntity> findAll() throws Exception {
        return eventCategoryRepository.findAll();
    }

    @Override
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

    @Override
    public void save(EventCategoryEntity eventCategoryEntity) throws Exception {
        eventCategoryRepository.save(eventCategoryEntity);
    }

    @Override
    public void deleteById(int id) throws Exception {
        eventCategoryRepository.deleteById(id);
    }
}
