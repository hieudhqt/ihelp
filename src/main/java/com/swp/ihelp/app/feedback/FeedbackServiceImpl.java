package com.swp.ihelp.app.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService{

    private FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<Feedback> findAll() throws Exception {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback findById(String id) throws Exception {
        Optional<Feedback> result = feedbackRepository.findById(id);
        Feedback feedback = null;
        if (result.isPresent()) {
            feedback = result.get();
        } else {
            throw new RuntimeException("Did not find feedback with id:" + id);
        }
        return feedback;
    }

    @Override
    public void save(Feedback feedback) throws Exception {
        feedbackRepository.save(feedback);
    }

    @Override
    public void deleteById(String id) throws Exception {
        feedbackRepository.deleteById(id);
    }
}
