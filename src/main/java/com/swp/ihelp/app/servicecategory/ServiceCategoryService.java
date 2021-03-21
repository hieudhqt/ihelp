package com.swp.ihelp.app.servicecategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceCategoryService {
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    public ServiceCategoryService(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    public List<ServiceCategoryEntity> findAll() throws Exception {
        return serviceCategoryRepository.findAll();
    }

    public ServiceCategoryEntity findById(int id) throws Exception {
        Optional<ServiceCategoryEntity> result = serviceCategoryRepository.findById(id);
        ServiceCategoryEntity serviceCategoryEntity = null;
        if (result.isPresent()) {
            serviceCategoryEntity = result.get();
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return serviceCategoryEntity;
    }

    public void save(ServiceCategoryEntity serviceCategoryEntity) throws Exception {
        serviceCategoryRepository.save(serviceCategoryEntity);
    }

    public void deleteById(int id) throws Exception {
        serviceCategoryRepository.deleteById(id);
    }
}
