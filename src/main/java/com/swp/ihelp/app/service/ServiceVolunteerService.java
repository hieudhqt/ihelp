package com.swp.ihelp.app.service;

import com.swp.ihelp.app.service.request.ServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;

import javax.validation.Valid;
import java.util.Map;

public interface ServiceVolunteerService {
    Map<String, Object> findAll(int page) throws Exception;

    ServiceDetailResponse findById(String id) throws Exception;

    Map<String, Object> findByTitle(String title, int page) throws Exception;

    void insert(@Valid ServiceRequest serviceRequest) throws Exception;

    void update(@Valid ServiceRequest serviceRequest) throws Exception;

    void patch(@Valid ServiceRequest serviceRequest) throws Exception;

    //    void update(ServiceEntity serviceEntity) throws Exception;
    void deleteById(String id) throws Exception;

    Map<String, Object> findByServiceTypeId(int id, int page) throws Exception;

    Map<String, Object> findByStatusId(int id, int page) throws Exception;

    Map<String, Object> findByAuthorEmail(String email, int page) throws Exception;

    void useService(String email, String serviceId) throws Exception;
}
