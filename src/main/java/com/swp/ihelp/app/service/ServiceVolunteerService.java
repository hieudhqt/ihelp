package com.swp.ihelp.app.service;

import com.swp.ihelp.app.service.request.CreateServiceRequest;
import com.swp.ihelp.app.service.request.RejectServiceRequest;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;

import javax.validation.Valid;
import java.util.Map;

public interface ServiceVolunteerService {
    Map<String, Object> findAll(int page) throws Exception;

    Map<String, Object> findAll(int page, String searchh) throws Exception;

    ServiceDetailResponse findById(String id) throws Exception;

    Map<String, Object> findByTitle(String title, int page) throws Exception;

    String insert(@Valid CreateServiceRequest request) throws Exception;

    ServiceDetailResponse update(@Valid UpdateServiceRequest request) throws Exception;

    void updateStatus(String serviceId, int statusId) throws Exception;

    void deleteById(String id) throws Exception;

    Map<String, Object> findByCategoryId(int id, int page) throws Exception;

    Map<String, Object> findByStatusId(int id, int page) throws Exception;

    Map<String, Object> findByAuthorEmail(String email, int page) throws Exception;

    ServiceEntity useService(String email, String serviceId) throws Exception;

    ServiceEntity approve(String eventId, String managerEmail) throws Exception;

    ServiceEntity reject(RejectServiceRequest request) throws Exception;

    Map<String, Object> findNearbyServices(int page, float radius, double lat, double lng) throws Exception;
}
