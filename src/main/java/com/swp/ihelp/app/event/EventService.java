package com.swp.ihelp.app.event;

import java.util.List;

public interface EventService {
    List<EventEntity> findAll() throws Exception;
    EventEntity findById(String id) throws Exception;
    List<EventEntity> findByTitle(String title) throws Exception;
    void save(EventEntity event) throws Exception;
    void deleteById(String id) throws Exception;
    List<EventEntity> findByCategoryId(int categoryId) throws Exception;
    List<EventEntity> findByStatusId(int statusId) throws Exception;
    List<EventEntity> findByAuthorEmail(String email) throws Exception;
    void joinEvent(String email, String eventId) throws Exception;
}
