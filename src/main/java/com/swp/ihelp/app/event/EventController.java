package com.swp.ihelp.app.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventResponse> findAll() throws Exception {
        return eventService.findAll();
    }

    @GetMapping("/events/{eventId}")
    public EventResponse findById(@PathVariable String eventId) throws Exception {
        return eventService.findById(eventId);
    }

    @GetMapping("/events/title/{eventTitle}")
    public List<EventResponse> findByTitle(@PathVariable String eventTitle) throws Exception {
        return eventService.findByTitle(eventTitle);
    }

    @GetMapping("/events/category/{categoryId}")
    public List<EventResponse> findByCategoryId(@PathVariable int categoryId) throws Exception {
        return eventService.findByCategoryId(categoryId);
    }

    @GetMapping("/events/status/{statusId}")
    public List<EventResponse> findByStatusId(@PathVariable int statusId) throws Exception {
        return eventService.findByStatusId(statusId);
    }

    @GetMapping("/events/account/{email}")
    public List<EventResponse> findByAuthorEmail(@PathVariable String email) throws Exception {
        return eventService.findByAuthorEmail(email);
    }

    @PostMapping("/events")
    public EventEntity addEvent(@RequestBody EventEntity event) throws Exception {
        eventService.save(event);
        return event;
    }

    @PutMapping("/events")
    public EventEntity updateEvent(@RequestBody EventEntity event) throws Exception {
        eventService.save(event);
        return event;
    }

    @DeleteMapping("/events/{eventId}")
    public String deleteEvent(@PathVariable String eventId) throws Exception {
        eventService.deleteById(eventId);
        return "Delete Event with ID: " + eventId;
    }

    @PostMapping("/events/{email}/{eventId}")
    public String joinEvent(@PathVariable String email, @PathVariable String eventId) throws Exception{
        eventService.joinEvent(email, eventId);
        return "Account with email:" + email + " has joined event with ID:" + eventId + ".";
    }
}
