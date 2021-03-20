package com.swp.ihelp.app.service;

import com.swp.ihelp.app.service.request.CreateServiceRequest;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findAll(page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/{serviceId}")
    public ServiceDetailResponse findById(@PathVariable String serviceId) throws Exception {
        return serviceVolunteerService.findById(serviceId);
    }

    @GetMapping("/services/title/{serviceTitle}")
    public ResponseEntity<Map<String, Object>> findByTitle(@PathVariable String serviceTitle,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByTitle(serviceTitle, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/services/type/{typeId}")
//    public ResponseEntity<Map<String, Object>> findByServiceTypeId(@PathVariable int typeId,
//                                                                   @RequestParam(value = "page") int page) throws Exception {
//        Map<String, Object> response = serviceVolunteerService.findByServiceTypeId(typeId, page);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/services/status/{statusId}")
    public ResponseEntity<Map<String, Object>> findByStatusId(@PathVariable int statusId,
                                                              @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByStatusId(statusId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/account/{email}")
    public ResponseEntity<Map<String, Object>> findByAuthorEmail(@PathVariable String email,
                                                                 @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByAuthorEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/services")
    public ResponseEntity<String> addService(@Valid @RequestBody CreateServiceRequest createServiceRequest) throws Exception {
        String serviceId = serviceVolunteerService.insert(createServiceRequest);
        return ResponseEntity.ok("Service with ID: " + serviceId + " added.");
    }

    @PutMapping("/services")
    public ResponseEntity<String> updateService(@RequestBody UpdateServiceRequest request) throws Exception {
        serviceVolunteerService.update(request);
        return ResponseEntity.ok("Service " + request.getId() + " updated.");
    }

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
