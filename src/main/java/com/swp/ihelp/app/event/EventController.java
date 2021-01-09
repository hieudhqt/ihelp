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
    public List<Event> findAll() throws Exception {
        return eventService.findAll();
    }

    @GetMapping("/events/{eventId}")
    public Event findById(@PathVariable String eventId) throws Exception {
        return eventService.findById(eventId);
    }

    @GetMapping("/events/title/{eventTitle}")
    public List<Event> findByTitle(@PathVariable String eventTitle) throws Exception {
        return eventService.findByTitle(eventTitle);
    }

    @PostMapping("/events")
    public Event addEvent(@RequestBody Event event) throws Exception {
        eventService.save(event);
        return event;
    }

    @PutMapping("/events")
    public Event updateEvent(@RequestBody Event event) throws Exception {
        eventService.save(event);
        return event;
    }

    @DeleteMapping("/events/{eventId}")
    public String deleteEvent(@PathVariable String eventId) throws Exception {
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Event id not found - " + eventId);
        }
        eventService.deleteById(eventId);
        return "Delete Event with ID: " + eventId;
    }
}
