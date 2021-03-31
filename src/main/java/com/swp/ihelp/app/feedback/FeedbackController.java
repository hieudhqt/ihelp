package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.feedback.request.FeedbackRequest;
import com.swp.ihelp.app.feedback.response.FeedbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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

    @PostMapping
    public void insert(@RequestBody FeedbackRequest request) throws Exception {
        feedbackService.insert(request);
    }

    @PutMapping("/{feedbackId}/{statusId}")
    public void updateStatus(@PathVariable String feedbackId, @PathVariable String statusId) throws Exception {
        feedbackService.updateStatus(feedbackId, statusId);
    }

    @GetMapping("/{statusId}")
    public List<FeedbackResponse> findByStatus(@PathVariable String statusId) throws Exception {
        return feedbackService.findByStatus(statusId);
    }

}
