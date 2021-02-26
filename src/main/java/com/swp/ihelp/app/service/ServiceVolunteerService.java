package com.swp.ihelp.app.service;

import com.swp.ihelp.app.service.request.ServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.app.service.response.ServiceResponse;

import javax.validation.Valid;
import java.util.List;

public interface ServiceVolunteerService {
    List<ServiceResponse> findAll() throws Exception;

    ServiceDetailResponse findById(String id) throws Exception;

    List<ServiceResponse> findByTitle(String title) throws Exception;

    void insert(@Valid ServiceRequest serviceRequest) throws Exception;

    void update(@Valid ServiceRequest serviceRequest) throws Exception;

    void patch(@Valid ServiceRequest serviceRequest) throws Exception;

    //    void update(ServiceEntity serviceEntity) throws Exception;
    void deleteById(String id) throws Exception;

    List<ServiceResponse> findByServiceTypeId(int id) throws Exception;

    List<ServiceResponse> findByStatusId(int id) throws Exception;

    List<ServiceResponse> findByAuthorEmail(String email) throws Exception;

    void useService(String email, String serviceId) throws Exception;
}
