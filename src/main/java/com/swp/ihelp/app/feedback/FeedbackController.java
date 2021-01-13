package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.event.Event;
import com.swp.ihelp.app.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {
    private FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/feedbacks")
    public List<Feedback> findAll() throws Exception {
        return feedbackService.findAll();
    }

    @GetMapping("/feedbacks/{feedbackId}")
    public Feedback findById(@PathVariable String feedbackId) throws Exception {
        return feedbackService.findById(feedbackId);
    }

    @PostMapping("/feedbacks")
    public Feedback addFeedback(@RequestBody Feedback feedback) throws Exception {
        feedbackService.save(feedback);
        return feedback;
    }

    @PutMapping("/feedbacks")
    public Feedback updateFeedback(@RequestBody Feedback feedback) throws Exception {
        feedbackService.save(feedback);
        return feedback;
    }

    @DeleteMapping("/feedbacks/{feedbackId}")
    public String deleteFeedback(@PathVariable String feedbackId) throws Exception {
        Feedback feedback = feedbackService.findById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("Feedback id not found - " + feedbackId);
        }
        feedbackService.deleteById(feedbackId);
        return "Delete Feedback with ID: " + feedbackId;
    }
}
