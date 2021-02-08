package com.swp.ihelp.app.service;

import java.util.List;

public interface ServiceVolunteerService {
    List<ServiceEntity> findAll() throws Exception;
    ServiceEntity findById(String id) throws Exception;
    List<ServiceEntity> findByTitle(String title) throws Exception;
    void save(ServiceEntity service) throws Exception;
    void deleteById(String id) throws Exception;
    List<ServiceEntity> findByServiceTypeId(int id) throws Exception;
    List<ServiceEntity> findByStatusId(int id) throws Exception;
}
