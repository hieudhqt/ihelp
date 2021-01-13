package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.event.Event;

import java.util.List;

public interface FeedbackService {
    List<Feedback> findAll() throws Exception;
    Feedback findById(String id) throws Exception;
    void save(Feedback feedback) throws Exception;
    void deleteById(String id) throws Exception;
}
