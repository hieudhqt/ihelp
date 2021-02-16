package com.swp.ihelp.app.eventcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventCategoryController {
    private EventCategoryService eventCategoryService;

    @Autowired
    public EventCategoryController(EventCategoryService eventCategoryService) {
        this.eventCategoryService = eventCategoryService;
    }

    @GetMapping("/event-category")
    public List<EventCategoryEntity> findAll() throws Exception {
        return eventCategoryService.findAll();
    }

    @GetMapping("/event-category/{categoryId}")
    public EventCategoryEntity findById(@PathVariable int categoryId) throws Exception {
        return eventCategoryService.findById(categoryId);
    }

    @PostMapping("/event-category")
    public EventCategoryEntity addEventCategory(@RequestBody EventCategoryEntity category) throws Exception {
        eventCategoryService.save(category);
        return category;
    }

    @PutMapping("/event-category")
    public EventCategoryEntity updateEventCategory(@RequestBody EventCategoryEntity category) throws Exception {
        eventCategoryService.save(category);
        return category;
    }

    @DeleteMapping("/event-category/{categoryId}")
    public String deleteEvent(@PathVariable int categoryId) throws Exception {
        EventCategoryEntity category = eventCategoryService.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Event category id not found - " + categoryId);
        }
        eventCategoryService.deleteById(categoryId);
        return "Delete Event category with ID: " + categoryId;
    }

}
