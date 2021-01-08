package com.swp.ihelp.app.servicevolunteer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceVolunteerController {
    private ServiceVolunteerService service;

    @Autowired
    public ServiceVolunteerController(ServiceVolunteerService service) {
        this.service = service;
    }

    @GetMapping("/services")
    public List<ServiceVolunteer> findAll() throws Exception{
        return service.findAll();
    }

    @GetMapping("/services/{serviceId}")
    public ServiceVolunteer findById(@PathVariable String serviceId) throws Exception{
        return service.findById(serviceId);
    }

    @GetMapping("/services/title/{serviceTitle}")
    public List<ServiceVolunteer> findByTitle(@PathVariable String serviceTitle) throws Exception{
        return service.findByTitle(serviceTitle);
    }

    @PostMapping("/services")
    public ServiceVolunteer addService(@RequestBody ServiceVolunteer serviceVolunteer) throws Exception{
        service.save(serviceVolunteer);
        return serviceVolunteer;
    }

    @PutMapping("/services")
    public ServiceVolunteer updateService(@RequestBody ServiceVolunteer serviceVolunteer) throws Exception{
        service.save(serviceVolunteer);
        return serviceVolunteer;
    }

    @DeleteMapping("/services/{serviceId}")
    public String deleteService(@PathVariable String serviceId) throws Exception {
        ServiceVolunteer serviceVolunteer = service.findById(serviceId);
        if (serviceVolunteer == null) {
            throw new RuntimeException("Service id not found - " + serviceId);
        }
        service.deleteById(serviceId);
        return "Delete ServiceVolunteer with ID: " + serviceId;
    }
}
