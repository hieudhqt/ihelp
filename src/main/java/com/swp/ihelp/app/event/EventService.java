package com.swp.ihelp.app.event;

import java.util.List;

public interface EventService {
    List<EventEntity> findAll() throws Exception;
    EventEntity findById(String id) throws Exception;
    List<EventEntity> findByTitle(String title) throws Exception;
    void save(EventEntity event) throws Exception;
    void deleteById(String id) throws Exception;
}
