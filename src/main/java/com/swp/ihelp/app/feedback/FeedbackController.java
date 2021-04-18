package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.request.RejectFeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<FeedbackResponse> findAll() throws Exception {
        return feedbackService.findAll();
    }

    @GetMapping("/{feedbackId}")
    public FeedbackResponse findById(@PathVariable String feedbackId) throws Exception {
        return feedbackService.findById(feedbackId);
    }

    @GetMapping("/status/{statusId}")
    public List<FeedbackResponse> findByStatus(@PathVariable String statusId) throws Exception {
        return feedbackService.findByStatus(statusId);
    }

    @GetMapping("/email/{email}")
    public List<FeedbackResponse> findByEmail(@PathVariable String email) throws Exception {
        return feedbackService.findByEmail(email);
    }

    @GetMapping("/event/{eventId}/status/{statusId}")
    public List<FeedbackResponse> findByEventId(@PathVariable String eventId, @PathVariable String statusId) throws Exception {
        return feedbackService.findByEventId(eventId, statusId);
    }

    @GetMapping("/service/{serviceId}/status/{statusId}")
    public List<FeedbackResponse> findByServiceId(@PathVariable String serviceId, @PathVariable String statusId) throws Exception {
        return feedbackService.findByServiceId(serviceId, statusId);
    }

    @GetMapping("/reports")
    public List<FeedbackResponse> getReports() throws Exception {
        return feedbackService.getReports();
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
