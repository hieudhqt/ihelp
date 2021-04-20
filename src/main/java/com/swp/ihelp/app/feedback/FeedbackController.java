package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@Api(tags = "Feedback Controller")
@RequestMapping("/api/feedbacks")
@RestController
public class FeedbackController {

    private FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
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
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> findByEventId(@PathVariable String eventId,
                                                             @RequestParam(required = false) Integer statusId,
                                                             @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByEventId(eventId, statusId, page), HttpStatus.OK);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<Map<String, Object>> findByServiceId(@PathVariable String serviceId,
                                                               @RequestParam(required = false) Integer statusId,
                                                               @RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.findByServiceId(serviceId, statusId, page), HttpStatus.OK);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports(@RequestParam(value = "page") int page) throws Exception {
        return new ResponseEntity<>(feedbackService.getReports(page), HttpStatus.OK);
    }

    @PostMapping
    public void insert(@RequestBody FeedbackRequest request) throws Exception {
        feedbackService.insert(request);
    }

    @PutMapping("/{feedbackId}/{statusId}")
    public void updateStatus(@PathVariable String feedbackId, @PathVariable String statusId) throws Exception {
        feedbackService.updateStatus(feedbackId, statusId);
    }

    @PutMapping("/{email}/approve/{feedbackId}")
    public ResponseEntity<String> approve(@PathVariable String email, @PathVariable String feedbackId) throws Exception {
        feedbackService.approve(feedbackId, email);
        return ResponseEntity.ok("Feedback " + feedbackId + " has been approved by " + email);
    }

    @PutMapping("/reject")
    public ResponseEntity<String> reject(@RequestBody RejectFeedbackRequest request) throws Exception {
        feedbackService.reject(request);
        return ResponseEntity.ok("Feedback " + request.getFeedbackId() +
                " has been rejected by " + request.getManagerEmail() + ", reason: "
                + request.getReason());
    }

}