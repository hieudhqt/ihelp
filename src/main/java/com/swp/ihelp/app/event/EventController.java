package com.swp.ihelp.app.event;

import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.EventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.message.EventMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EventController {
    private EventService eventService;

    @Autowired
    private EventMessage eventMessage;

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
    public ResponseEntity<String> addEvent(@Valid @RequestBody EventRequest eventRequest) throws Exception {
        eventService.save(eventRequest);
        return ResponseEntity.ok(eventMessage.getEventAddedMessage());
    }

    @PostMapping("/events/evaluation")
    public ResponseEntity<String> evaluateMember(@Valid @RequestBody EvaluationRequest request)
            throws Exception {
        eventService.evaluateMember(request);
        return ResponseEntity.ok("Evaluation completed.");
    }

    @PutMapping("/events")
    public ResponseEntity<String> updateEvent(@Valid @RequestBody EventRequest eventRequest) throws Exception {
        eventService.save(eventRequest);
        return ResponseEntity.ok(eventMessage.getEventUpdatedMessage(eventRequest.getId()));
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

    @GetMapping("/events/testjson")
    public ResponseEntity<String> testJSONObject() throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", "john doe");
        JSONObject childObject = new JSONObject();
        childObject.put("abc", "xyz");
        json.put("child", childObject);
        return ResponseEntity.ok(json.toString(2));
    }


}


