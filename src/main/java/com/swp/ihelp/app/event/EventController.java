package com.swp.ihelp.app.event;

import com.swp.ihelp.app.event.request.CreateEventRequest;
import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.UpdateEventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventResponse;
import com.swp.ihelp.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EventController {
    private EventService eventService;

    @Autowired
    private EventMessage eventMessage;

    @Autowired
    private EventRepository repository;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findAll(page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}")
    public EventDetailResponse findById(@PathVariable String eventId) throws Exception {
        return eventService.findById(eventId);
    }

    @GetMapping("/events/title/{eventTitle}")
    public ResponseEntity<Map<String, Object>> findByTitle(@PathVariable String eventTitle,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByTitle(eventTitle, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> findByCategoryId(@PathVariable int categoryId,
                                                                @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByCategoryId(categoryId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/status/{statusId}")
    public ResponseEntity<Map<String, Object>> findByStatusId(@PathVariable int statusId,
                                                              @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByStatusId(statusId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/account/{email}")
    public ResponseEntity<Map<String, Object>> findByAuthorEmail(@PathVariable String email,
                                                                 @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByAuthorEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/history/{email}/{statusId}")
    public ResponseEntity<Map<String, Object>> findByAuthorEmail(@PathVariable String email,
                                                                 @PathVariable int statusId,
                                                                 @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByParticipantEmail(email, statusId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@Valid @RequestBody CreateEventRequest eventRequest) throws Exception {
        String eventId = eventService.insert(eventRequest);
        return ResponseEntity.ok(eventMessage.getEventAddedMessage(eventId));
    }

    @PostMapping("/events/evaluation")
    public ResponseEntity<String> evaluateMember(@Valid @RequestBody EvaluationRequest request)
            throws Exception {
        eventService.evaluateMember(request);
        return ResponseEntity.ok("Evaluation completed.");
    }

    @PutMapping("/events")
    public ResponseEntity<EventDetailResponse> updateEvent(@Valid @RequestBody UpdateEventRequest eventRequest) throws Exception {
        EventDetailResponse updatedEvent = eventService.update(eventRequest);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }

    @PutMapping("/events/{eventId}/{statusId}")
    public ResponseEntity<String> updateStatus(@PathVariable String eventId,
                                               @PathVariable int statusId) throws Exception {
        eventService.updateStatus(eventId, statusId);
        return ResponseEntity.ok(eventMessage.getEventUpdatedMessage(eventId));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) throws Exception {
        eventService.deleteById(eventId);
        return ResponseEntity.ok(eventMessage.getEventDeletedMessage() + eventId);
    }

    @PostMapping("/events/{email}/{eventId}")
    public ResponseEntity<String> joinEvent(@PathVariable String email, @PathVariable String eventId) throws Exception {
        eventService.joinEvent(email, eventId);
        return ResponseEntity.ok(eventMessage.getEventJoinedMessage(eventId, email));
    }

    @GetMapping("/events/test")
    public ResponseEntity<Map<String, Object>> search(@RequestParam(value = "search", required = false) String search,
                                                      @RequestParam(value = "page") int page) {
        EventSpecificationBuilder builder = new EventSpecificationBuilder();
        String dateRegex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
        String emailRegex = "(^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$)";
        String textRegex = "([a-zA-Z0-9_ ]*$)";
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(" + textRegex + "|" + dateRegex + "|" + emailRegex + "),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        Pageable paging = PageRequest.of(page, 10);
        Specification<EventEntity> spec = builder.build();
        Page<EventEntity> pageEvent = repository.findAll(spec, paging);

        List<EventEntity> eventEntityList = pageEvent.getContent();
        List<EventResponse> eventResponses = EventResponse.convertToResponseList(eventEntityList);

        Map<String, Object> response = new HashMap<>();
        response.put("events", eventResponses);
        response.put("currentPage", pageEvent.getNumber());
        response.put("totalItems", pageEvent.getTotalElements());
        response.put("totalPages", pageEvent.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


