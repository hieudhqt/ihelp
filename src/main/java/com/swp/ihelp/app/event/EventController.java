package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.request.CreateEventRequest;
import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.RejectEventRequest;
import com.swp.ihelp.app.event.request.UpdateEventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationService;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import com.swp.ihelp.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EventController {

    private EventService eventService;

    private NotificationService notificationService;

    private PushNotificationService pushNotificationService;

    @Autowired
    private EventMessage eventMessage;

    @Autowired
    public EventController(EventService eventService, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findAll(page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/filter")
    public ResponseEntity<Map<String, Object>> findAllWithFilter(@RequestParam(value = "search", required = false)
                                                                         String search,
                                                                 @RequestParam(value = "page") int page)
            throws Exception {
        Map<String, Object> response = eventService.findAll(page, search);
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
    public ResponseEntity<Map<String, Object>> findByParticipantEmail(@PathVariable String email,
                                                                      @PathVariable(required = false) Integer statusId,
                                                                      @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findByParticipantEmail(email, statusId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/nearby/{radius}")
    public ResponseEntity<Map<String, Object>> findNearbyEvents(@PathVariable float radius,
                                                                @RequestParam(value = "lat") double lat,
                                                                @RequestParam(value = "lng") double lng,
                                                                @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = eventService.findNearbyEvents(page, radius, lat, lng);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/events/statistic/{year}")
    public ResponseEntity<Map<Integer, Integer>> getMonthlyHostedEventNumber(@PathVariable int year) throws Exception {
        return new ResponseEntity<>(eventService.getMonthlyHostedEventNumber(year), HttpStatus.OK);
    }

    @GetMapping("/events/account/{email}/{eventId}")
    public ResponseEntity<Boolean> isUserHasEnoughPoint(@PathVariable String email,
                                                        @PathVariable String eventId) throws Exception {
        return ResponseEntity.ok(eventService.isUserHasEnoguhPoint(email, eventId));
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

        //Push notification to evaluated member
        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(request.getMemberEmail());
        if (deviceTokens != null && !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle("You have been evaluated from event \"" + request.getTitle() + "\" host")
                    .setMessage("You now can give feedback about event \"" + request.getTitle() + "\"")
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        notificationService.insert(new NotificationEntity()
                .setTitle("You have been evaluated from event \"" + request.getTitle() + "\" host")
                .setMessage("You now can give feedback about event \"" + request.getTitle() + "\"")
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(request.getMemberEmail())));

        return ResponseEntity.ok("Evaluation of account " + request.getMemberEmail() + " completed.");
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

    @PutMapping("/events/{email}/approve/{eventId}")
    public ResponseEntity<String> approve(@PathVariable String email,
                                          @PathVariable String eventId) throws Exception {
        EventEntity approvedEvent = eventService.approve(eventId, email);

        //Push notification to event's host
        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(approvedEvent.getAuthorAccount().getEmail());
        if (deviceTokens != null && !deviceTokens.isEmpty()) {

            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("evaluateRequiredEvents", eventId);
            notificationData.put("managerEmail", email);

            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle("Your event: \"" + approvedEvent.getTitle() + "\" has been approved")
                    .setMessage("Event \"" + approvedEvent.getTitle() + "\" is now able to accept registration")
                    .setData(notificationData)
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        notificationService.insert(new NotificationEntity()
                .setTitle("Your event: \"" + approvedEvent.getTitle() + "\" has been approved")
                .setMessage("Event \"" + approvedEvent.getTitle() + " is now able to accept registration")
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(approvedEvent.getAuthorAccount().getEmail())));

        return ResponseEntity.ok("Event " + eventId + " has been approved by " + email);
    }

    @PutMapping("/events/reject")
    public ResponseEntity<String> reject(@Valid @RequestBody RejectEventRequest request) throws Exception {
        EventEntity rejectedEvent = eventService.reject(request);

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(rejectedEvent.getAuthorAccount().getEmail());
        if (deviceTokens != null & !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle("Your event: \"" + rejectedEvent.getTitle() + "\" has been rejected")
                    .setMessage("Event \"" + rejectedEvent.getTitle() + " has been rejected with following reason: " + request.getReason())
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        notificationService.insert(new NotificationEntity()
                .setTitle("Your event: \"" + rejectedEvent.getTitle() + "\" has been rejected")
                .setMessage("Event \"" + rejectedEvent.getTitle() + " has been rejected with following reason: " + request.getReason())
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(rejectedEvent.getAuthorAccount().getEmail())));

        return ResponseEntity.ok("Event " + request.getEventId() +
                " has been rejected by " + request.getManagerEmail() + ", reason: "
                + request.getReason());
    }

    @PutMapping("/events/enable/{eventId}")
    public ResponseEntity<String> enableEvent(@PathVariable String eventId) throws Exception {
        String updatedStatus = eventService.enableEvent(eventId);
        return ResponseEntity.ok("Event " + eventId + " enabled. " +
                "Status is changed to \"" + updatedStatus + "\"");
    }

    @PutMapping("/events/disable/{eventId}")
    public ResponseEntity<String> disableEvent(@PathVariable String eventId) throws Exception {
        eventService.disableEvent(eventId);
        return ResponseEntity.ok("Event " + eventId + " disabled.");
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) throws Exception {
        eventService.deleteById(eventId);
        return ResponseEntity.ok(eventMessage.getEventDeletedMessage() + eventId);
    }

    @DeleteMapping("/events/{eventId}/{email}")
    public ResponseEntity<String> quitEvent(@PathVariable String eventId,
                                            @PathVariable String email) throws Exception {
        eventService.quitEvent(eventId, email);
        return ResponseEntity.ok(eventMessage.getEventQuitedMessage(email, eventId));
    }

    @PostMapping("/events/{email}/{eventId}")
    public ResponseEntity<String> joinEvent(@PathVariable String email,
                                            @PathVariable String eventId) throws Exception {
        eventService.joinEvent(email, eventId);

        //Subscribe user to topic with eventId in argument
        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(email);
        if (deviceTokens != null && !deviceTokens.isEmpty()) {
            pushNotificationService.subscribeToTopic(deviceTokens, eventId);
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }
        return ResponseEntity.ok(eventMessage.getEventJoinedMessage(eventId, email));
    }
}


