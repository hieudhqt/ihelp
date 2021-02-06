package com.swp.ihelp.app.servicetype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceTypeService {
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public List<ServiceTypeEntity> findAll() throws Exception {
        return serviceTypeRepository.findAll();
    }

    public ServiceTypeEntity findById(int id) throws Exception {
        Optional<ServiceTypeEntity> result = serviceTypeRepository.findById(id);
        ServiceTypeEntity serviceTypeEntity = null;
        if (result.isPresent()) {
            serviceTypeEntity = result.get();
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return serviceTypeEntity;
    }

    public void save(ServiceTypeEntity serviceTypeEntity) throws Exception {
        serviceTypeRepository.save(serviceTypeEntity);
    }

    public void deleteById(int id) throws Exception {
        serviceTypeRepository.deleteById(id);
    }
}
