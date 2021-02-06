package com.swp.ihelp.app.servicetype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceTypeController {
    private ServiceTypeService serviceTypeService;

    @Autowired
    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @GetMapping("/service-type")
    public List<ServiceTypeEntity> findAll() throws Exception {
        return serviceTypeService.findAll();
    }

    @GetMapping("/service-type/{typeId}")
    public ServiceTypeEntity findById(@PathVariable int typeId) throws Exception {
        return serviceTypeService.findById(typeId);
    }

    @PostMapping("/service-type")
    public ServiceTypeEntity addServiceType(@RequestBody ServiceTypeEntity type) throws Exception {
        serviceTypeService.save(type);
        return type;
    }

    @PutMapping("/service-type")
    public ServiceTypeEntity updateEventCategory(@RequestBody ServiceTypeEntity type) throws Exception {
        serviceTypeService.save(type);
        return type;
    }

    @DeleteMapping("/service-type/{typeId}")
    public String deleteEvent(@PathVariable int typeId) throws Exception {
        ServiceTypeEntity category = serviceTypeService.findById(typeId);
        if (category == null) {
            throw new RuntimeException("Service type ID not found - " + typeId);
        }
        serviceTypeService.deleteById(typeId);
        return "Delete service type with ID: " + typeId;
    }
}
