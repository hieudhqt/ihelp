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
    public List<EventEntity> findAll() throws Exception {
        return eventService.findAll();
    }

    @GetMapping("/events/{eventId}")
    public EventEntity findById(@PathVariable String eventId) throws Exception {
        return eventService.findById(eventId);
    }

    @GetMapping("/events/title/{eventTitle}")
    public List<EventEntity> findByTitle(@PathVariable String eventTitle) throws Exception {
        return eventService.findByTitle(eventTitle);
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
        EventEntity event = eventService.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Event id not found - " + eventId);
        }
        eventService.deleteById(eventId);
        return "Delete Event with ID: " + eventId;
    }
}
