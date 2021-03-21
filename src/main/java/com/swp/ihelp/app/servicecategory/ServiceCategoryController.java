package com.swp.ihelp.app.servicecategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ServiceCategoryController {
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    @GetMapping("/service-type")
    public List<ServiceCategoryEntity> findAll() throws Exception {
        return serviceCategoryService.findAll();
    }

    @GetMapping("/service-type/{typeId}")
    public ServiceCategoryEntity findById(@PathVariable int typeId) throws Exception {
        return serviceCategoryService.findById(typeId);
    }

    @PostMapping("/service-type")
    public ServiceCategoryEntity addServiceType(@RequestBody ServiceCategoryEntity type) throws Exception {
        serviceCategoryService.save(type);
        return type;
    }

    @PutMapping("/service-type")
    public ServiceCategoryEntity updateEventCategory(@RequestBody ServiceCategoryEntity type) throws Exception {
        serviceCategoryService.save(type);
        return type;
    }

    @DeleteMapping("/service-type/{typeId}")
    public String deleteEvent(@PathVariable int typeId) throws Exception {
        ServiceCategoryEntity category = serviceCategoryService.findById(typeId);
        if (category == null) {
            throw new RuntimeException("Service type ID not found - " + typeId);
        }
        serviceCategoryService.deleteById(typeId);
        return "Delete service type with ID: " + typeId;
    }
}
