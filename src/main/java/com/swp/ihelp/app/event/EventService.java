package com.swp.ihelp.app.event;

import com.swp.ihelp.app.event.request.EventRequest;
import com.swp.ihelp.app.event.response.EventDetailResponse;
import com.swp.ihelp.app.event.response.EventResponse;

import java.util.List;

public interface EventService {
    List<EventResponse> findAll() throws Exception;

    EventDetailResponse findById(String id) throws Exception;

    List<EventResponse> findByTitle(String title) throws Exception;

    void save(EventRequest event) throws Exception;

    void deleteById(String id) throws Exception;

    List<EventResponse> findByCategoryId(int categoryId) throws Exception;

    List<EventResponse> findByStatusId(int statusId) throws Exception;

    List<EventResponse> findByAuthorEmail(String email) throws Exception;

    void joinEvent(String email, String eventId) throws Exception;
}
