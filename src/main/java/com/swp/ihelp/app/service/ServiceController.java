package com.swp.ihelp.app.service;

import com.swp.ihelp.app.service.request.ServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.app.service.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
@CrossOrigin
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
    public ServiceDetailResponse findById(@PathVariable String serviceId) throws Exception {
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
    public ResponseEntity<String> addService(@Valid @RequestBody ServiceRequest serviceRequest) throws Exception {
        serviceVolunteerService.insert(serviceRequest);
        return ResponseEntity.ok("Service added.");
    }

    @PutMapping("/services")
    public ResponseEntity<String> updateService(@RequestBody ServiceRequest serviceRequest) throws Exception {
        serviceVolunteerService.update(serviceRequest);
        return ResponseEntity.ok("Service " + serviceRequest.getId() + " updated.");
    }

    @PatchMapping("/services")
    public ResponseEntity<String> patchService(@RequestBody ServiceRequest serviceRequest) throws Exception {
        serviceVolunteerService.patch(serviceRequest);
        return ResponseEntity.ok("Service " + serviceRequest.getId() + " patched.");
    }

//    @PutMapping("/services")
//    public ResponseEntity<String> updateService(@RequestBody ServiceEntity entity) throws Exception {
//        serviceVolunteerService.update(entity);
//        return ResponseEntity.ok("Service " + entity.getId() + " updated.");
//    }

    @DeleteMapping("/services/{serviceId}")
    public String deleteService(@PathVariable String serviceId) throws Exception {
        ServiceDetailResponse response = serviceVolunteerService.findById(serviceId);
        if (response == null) {
            throw new RuntimeException("Could not find service:" + serviceId);
        }
        serviceVolunteerService.deleteById(serviceId);
        return "Service deleted." + serviceId;
    }

    @PostMapping("/services/{email}/{serviceId}")
    public String useService(@PathVariable String email, @PathVariable String serviceId) throws Exception {
        serviceVolunteerService.useService(email, serviceId);
        return "Account with email:" + email + " has used service with ID:" + serviceId + ".";
    }
}
