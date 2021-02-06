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
    public List<ServiceEntity> findAll() throws Exception {
        return serviceVolunteerService.findAll();
    }

    @GetMapping("/services/{serviceId}")
    public ServiceEntity findById(@PathVariable String serviceId) throws Exception {
        return serviceVolunteerService.findById(serviceId);
    }

    @GetMapping("/services/title/{serviceTitle}")
    public List<ServiceEntity> findByTitle(@PathVariable String serviceTitle) throws Exception {
        return serviceVolunteerService.findByTitle(serviceTitle);
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
        ServiceEntity event = serviceVolunteerService.findById(serviceId);
        if (event == null) {
            throw new RuntimeException("Service id not found - " + serviceId);
        }
        serviceVolunteerService.deleteById(serviceId);
        return "Delete Event with ID: " + serviceId;
    }
}
