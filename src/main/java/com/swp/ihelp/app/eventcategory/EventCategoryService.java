package com.swp.ihelp.app.eventcategory;

import java.util.List;

public interface EventCategoryService {
    List<EventCategoryEntity> findAll() throws Exception;

    EventCategoryEntity findById(int id) throws Exception;

    void save(EventCategoryEntity eventCategoryEntity) throws Exception;

    void deleteById(int id) throws Exception;
}
