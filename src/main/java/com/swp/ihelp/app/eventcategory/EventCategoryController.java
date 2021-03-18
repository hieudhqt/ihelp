package com.swp.ihelp.app.eventcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EventCategoryController {
    private EventCategoryService eventCategoryService;

    @Autowired
    public EventCategoryController(EventCategoryServiceImpl eventCategoryServiceImpl) {
        this.eventCategoryService = eventCategoryServiceImpl;
    }

    @GetMapping("/event-categories")
    public List<EventCategoryEntity> findAll() throws Exception {
        return eventCategoryService.findAll();
    }

    @GetMapping("/event-categories/{categoryId}")
    public EventCategoryEntity findById(@PathVariable int categoryId) throws Exception {
        return eventCategoryService.findById(categoryId);
    }

    @PostMapping("/event-categories")
    public EventCategoryEntity addEventCategory(@RequestBody EventCategoryEntity category) throws Exception {
        eventCategoryService.save(category);
        return category;
    }

    @PutMapping("/event-categories")
    public EventCategoryEntity updateEventCategory(@RequestBody EventCategoryEntity category) throws Exception {
        eventCategoryService.save(category);
        return category;
    }

    @DeleteMapping("/event-categories/{categoryId}")
    public String deleteEvent(@PathVariable int categoryId) throws Exception {
        EventCategoryEntity category = eventCategoryService.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Event category id not found - " + categoryId);
        }
        eventCategoryService.deleteById(categoryId);
        return "Delete Event category with ID: " + categoryId;
    }

}
