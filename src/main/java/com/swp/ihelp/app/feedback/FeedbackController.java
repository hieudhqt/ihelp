package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationService;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Api(tags = "Feedback Controller")
@RequestMapping("/api/feedbacks")
@RestController
public class FeedbackController {

    private FeedbackService feedbackService;

    private NotificationService notificationService;

    private PushNotificationService pushNotificationService;

    public FeedbackController(FeedbackService feedbackService, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findAll(page), HttpStatus.OK);
    }

    @GetMapping("/{feedbackId}")
    public FeedbackResponse findById(@PathVariable String feedbackId) throws Exception {
        return feedbackService.findById(feedbackId);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<Map<String, Object>> findByStatus(@PathVariable Integer statusId,
                                                            @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByStatus(statusId, page), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> findByEmail(@PathVariable String email,
                                                           @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByEmail(email, page), HttpStatus.OK);
    }

    //    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "eventId", value = "Event ID",dataTypeClass = String.class, paramType = "path"),
//            @ApiImplicitParam(name = "statusId", dataTypeClass = Integer.class, paramType = "path", required = false)
//    })
    @GetMapping("/event/{eventId}/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> findByEventId(@PathVariable String eventId,
                                                             @PathVariable Integer categoryId,
                                                             @RequestParam(required = false) Integer statusId,
                                                             @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByEventId(eventId, categoryId, statusId, page), HttpStatus.OK);
    }

    @GetMapping("/service/{serviceId}/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> findByServiceId(@PathVariable String serviceId,
                                                               @PathVariable Integer categoryId,
                                                               @RequestParam(required = false) Integer statusId,
                                                               @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByServiceId(serviceId, categoryId, statusId, page), HttpStatus.OK);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports(@RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.getReports(page), HttpStatus.OK);
    }

    @GetMapping("/eventId/{eventId}/email/{email}/exist")
    public ResponseEntity<Map<String, Object>> existsByEventIdAndEmail(@PathVariable String eventId, @PathVariable String email) throws Exception {
        boolean isExisted = feedbackService.existsByEventIdAndEmail(eventId, email);
        Map<String, Object> map = new HashMap<>();
        map.put("isExisted", isExisted);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/service/{serviceId}/email/{email}/exist")
    public ResponseEntity<Map<String, Object>> existsByServiceIdAndEmail(@PathVariable String serviceId, @PathVariable String email) throws Exception {
        boolean isExisted = feedbackService.existsByServiceIdAndEmail(serviceId, email);
        Map<String, Object> map = new HashMap<>();
        map.put("isExisted", isExisted);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping
    public void insert(@RequestBody FeedbackRequest request) throws Exception {
        FeedbackEntity savedFeedback = feedbackService.insert(request);
    }

    @PutMapping("/{feedbackId}/{statusId}")
    public void updateStatus(@PathVariable String feedbackId, @PathVariable String statusId) throws Exception {
        feedbackService.updateStatus(feedbackId, statusId);
    }

    @PutMapping("/{email}/approve/{feedbackId}")
    public ResponseEntity<String> approve(@PathVariable String email, @PathVariable String feedbackId) throws Exception {
        FeedbackEntity approvedFeedback = feedbackService.approve(feedbackId, email);

        String authorTitle = "Approved feedback";
        String authorMessage = "Approved feedback";

        String hostTitle = "New feedback";
        String hostMessage = "New feedback";
        String hostEmail = "";

        if (approvedFeedback.getEvent() != null) {
            authorTitle = "Feedback for event \"" + approvedFeedback.getEvent().getTitle() + "\" has been approved";
            authorMessage = "Your feedback has been approved";

            hostTitle = "New feedback for event \"" + approvedFeedback.getEvent().getTitle() + "\"";
            hostMessage = "You have a new feedback for event";
            hostEmail = approvedFeedback.getEvent().getAuthorAccount().getEmail();
        } else if (approvedFeedback.getService() != null) {
            authorTitle = "Feedback for service \"" + approvedFeedback.getService().getTitle() + "\" has been approved";
            authorMessage = "Your feedback has been approved";

            hostTitle = "New feedback for service \"" + approvedFeedback.getService().getTitle() + "\"";
            hostMessage = "You have a new feedback for service";
            hostEmail = approvedFeedback.getService().getAuthorAccount().getEmail();
        }

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(approvedFeedback.getAccount().getEmail());
        if (deviceTokens != null && !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle(authorTitle)
                    .setMessage(authorMessage)
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        }

        notificationService.insert(new NotificationEntity()
                .setTitle(authorTitle)
                .setMessage(authorMessage)
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(approvedFeedback.getAccount().getEmail())));

        List<String> hostDeviceTokens = notificationService.findDeviceTokensByEmail(hostEmail);
        if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle(hostTitle)
                    .setMessage(hostMessage)
                    .setRegistrationTokens(hostDeviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        }

        notificationService.insert(new NotificationEntity()
                .setTitle(hostTitle)
                .setMessage(hostMessage)
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(hostEmail)));

        return ResponseEntity.ok("Feedback " + feedbackId + " has been approved by " + email);
    }

    @PutMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody RejectFeedbackRequest request) throws Exception {
        FeedbackEntity rejectedFeedback = feedbackService.reject(request);

        String title = "Rejected feedback";
        String message = "Rejected feedback";

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(rejectedFeedback.getAccount().getEmail());
        if (deviceTokens != null && !deviceTokens.isEmpty()) {

            if (rejectedFeedback.getEvent() != null) {
                title = "Feedback for event \"" + rejectedFeedback.getService().getTitle() + "\" has been rejected";
                message = "Your feedback has been rejected";
            } else if (rejectedFeedback.getService() != null) {
                title = "Feedback for service \"" + rejectedFeedback.getService().getTitle() + "\" has been rejected";
                message = "Your feedback has been rejected";
            }

            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle(title)
                    .setMessage(message)
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);
        }

        notificationService.insert(new NotificationEntity()
                .setTitle(title)
                .setMessage(message)
                .setDate(new Timestamp(System.currentTimeMillis()))
                .setAccountEntity(new AccountEntity().setEmail(rejectedFeedback.getAccount().getEmail())));

        return ResponseEntity.ok("Feedback " + request.getFeedbackId() +
                " has been rejected by " + request.getManagerEmail() + ", reason: "
                + request.getReason());
    }

}