package com.swp.ihelp.app.event;

import java.util.List;

public interface EventService {
    List<Event> findAll() throws Exception;
    Event findById(String id) throws Exception;
    List<Event> findByTitle(String title) throws Exception;
    void save(Event serviceVolunteer) throws Exception;
    void deleteById(String id) throws Exception;
}
