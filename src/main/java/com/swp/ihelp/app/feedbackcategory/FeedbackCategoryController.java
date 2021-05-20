package com.swp.ihelp.app.feedbackcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback-categories")
public class FeedbackCategoryController {

    private FeedbackCategoryService feedbackCategoryService;

    @Autowired
    public FeedbackCategoryController(FeedbackCategoryService feedbackCategoryService) {
        this.feedbackCategoryService = feedbackCategoryService;
    }

    @GetMapping
    public List<FeedbackCategoryEntity> findAll() throws Exception {
        return feedbackCategoryService.findAll();
    }

    @GetMapping("/{id}")
    public FeedbackCategoryEntity findById(@PathVariable Integer id) throws Exception {
        return feedbackCategoryService.findById(id);
    }
}
