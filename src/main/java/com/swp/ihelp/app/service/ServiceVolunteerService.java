package com.swp.ihelp.app.service;

import java.util.List;

public interface ServiceVolunteerService {
    List<ServiceResponse> findAll() throws Exception;

    ServiceResponse findById(String id) throws Exception;

    List<ServiceResponse> findByTitle(String title) throws Exception;

    void save(ServiceEntity service) throws Exception;

    void deleteById(String id) throws Exception;

    List<ServiceResponse> findByServiceTypeId(int id) throws Exception;

    List<ServiceResponse> findByStatusId(int id) throws Exception;

    List<ServiceResponse> findByAuthorEmail(String email) throws Exception;

    void useService(String email, String serviceId) throws Exception;
}
