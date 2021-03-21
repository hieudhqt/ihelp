package com.swp.ihelp.app.event;

import com.swp.ihelp.app.event.request.CreateEventRequest;
import com.swp.ihelp.app.event.request.EvaluationRequest;
import com.swp.ihelp.app.event.request.UpdateEventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;

import java.util.Map;

public interface EventService {
    Map<String, Object> findAll(int page) throws Exception;

    EventDetailResponse findById(String id) throws Exception;

    Map<String, Object> findByTitle(String title, int page) throws Exception;

    void insert(CreateEventRequest event) throws Exception;

    void update(UpdateEventRequest event) throws Exception;

    void deleteById(String id) throws Exception;

    Map<String, Object> findByCategoryId(int categoryId, int page) throws Exception;

    Map<String, Object> findByStatusId(int statusId, int page) throws Exception;

    Map<String, Object> findByAuthorEmail(String email, int page) throws Exception;

    Map<String, Object> findByParticipantEmail(String email, int statusId, int page) throws Exception;

    void joinEvent(String email, String eventId) throws Exception;

    void updateStatus(String eventId, int statusId) throws Exception;

    void evaluateMember(EvaluationRequest request) throws Exception;
}
