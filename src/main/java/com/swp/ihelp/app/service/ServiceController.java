package com.swp.ihelp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {
    private ServiceVolunteerService serviceVolunteerService;

    @Autowired
    public ServiceController(ServiceVolunteerService serviceVolunteerService) {
        this.serviceVolunteerService = serviceVolunteerService;
    }

    @GetMapping("/services")
    public List<ServiceResponse> findAll() throws Exception {
        return serviceVolunteerService.findAll();
    }

    @GetMapping("/services/{serviceId}")
    public ServiceResponse findById(@PathVariable String serviceId) throws Exception {
        return serviceVolunteerService.findById(serviceId);
    }

    @GetMapping("/services/title/{serviceTitle}")
    public List<ServiceResponse> findByTitle(@PathVariable String serviceTitle) throws Exception {
        return serviceVolunteerService.findByTitle(serviceTitle);
    }

    @GetMapping("/services/type/{typeId}")
    public List<ServiceResponse> findByServiceTypeId(@PathVariable int typeId) throws Exception {
        return serviceVolunteerService.findByServiceTypeId(typeId);
    }

    @GetMapping("/services/status/{statusId}")
    public List<ServiceResponse> findByStatusId(@PathVariable int statusId) throws Exception {
        return serviceVolunteerService.findByStatusId(statusId);
    }

    @GetMapping("/services/account/{email}")
    public List<ServiceResponse> findByAuthorEmail(@PathVariable String email) throws Exception {
        return serviceVolunteerService.findByAuthorEmail(email);
    }

    @PostMapping("/services")
    public ServiceEntity addService(@RequestBody ServiceEntity service) throws Exception {
        serviceVolunteerService.save(service);
        return service;
    }

    @PutMapping("/services")
    public ServiceEntity updateService(@RequestBody ServiceEntity service) throws Exception {
        serviceVolunteerService.save(service);
        return service;
    }

    @DeleteMapping("/services/{serviceId}")
    public String deleteService(@PathVariable String serviceId) throws Exception {
        ServiceResponse serviceResponse = serviceVolunteerService.findById(serviceId);
        if (serviceResponse == null) {
            throw new RuntimeException("Service id not found - " + serviceId);
        }
        serviceVolunteerService.deleteById(serviceId);
        return "Delete Event with ID: " + serviceId;
    }

    @PostMapping("/services/{email}/{serviceId}")
    public String useService(@PathVariable String email, @PathVariable String serviceId) throws Exception {
        serviceVolunteerService.useService(email, serviceId);
        return "Account with email:" + email + " has used service with ID:" + serviceId + ".";
    }
}
