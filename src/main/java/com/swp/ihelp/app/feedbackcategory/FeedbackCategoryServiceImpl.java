package com.swp.ihelp.app.feedbackcategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackCategoryServiceImpl implements FeedbackCategoryService {

    private FeedbackCategoryRepository feedbackCategoryRepository;

    @Autowired
    public FeedbackCategoryServiceImpl(FeedbackCategoryRepository feedbackCategoryRepository) {
        this.feedbackCategoryRepository = feedbackCategoryRepository;
    }

    @Override
    public List<FeedbackCategoryEntity> findAll() throws Exception {
        return feedbackCategoryRepository.findAll();
    }

    @Override
    public FeedbackCategoryEntity findById(Integer id) throws Exception {
        Optional<FeedbackCategoryEntity> result = feedbackCategoryRepository.findById(id);
        FeedbackCategoryEntity feedbackCategory = null;
        if (result.isPresent()) {
            feedbackCategory = result.get();
        } else {
            throw new RuntimeException("Did not find feedback category with ID: " + id);
        }
        return feedbackCategory;
    }
}
