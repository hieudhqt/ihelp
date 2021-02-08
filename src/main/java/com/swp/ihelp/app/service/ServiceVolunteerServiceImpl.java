package com.swp.ihelp.app.service;

import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceVolunteerServiceImpl implements ServiceVolunteerService {

    private ServiceRepository serviceRepository;

    @Autowired
    public ServiceVolunteerServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ServiceEntity> findAll() throws Exception {
        return serviceRepository.findAll();
    }

    @Override
    public ServiceEntity findById(String id) throws Exception {
        Optional<ServiceEntity> result = serviceRepository.findById(id);
        ServiceEntity service = null;
        if (result.isPresent()) {
            service = result.get();
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return service;
    }

    @Override
    public List<ServiceEntity> findByTitle(String title) throws Exception {
        return serviceRepository.findByTitle(title);
    }

    @Override
    public void save(ServiceEntity service) throws Exception {
        // Set createDate as current date for new event.
        if (service.getId() != null) {
            Optional<ServiceEntity> result = serviceRepository.findById(service.getId());
            if (result.isEmpty()) {
                service.setCreatedDate(new Date().getTime());
            }
        }

        serviceRepository.save(service);
    }

    @Override
    public void deleteById(String id) throws Exception {
        serviceRepository.deleteById(id);
    }

    @Override
    public List<ServiceEntity> findByServiceTypeId(int serviceTypeId) throws Exception {
        List<ServiceEntity> result = serviceRepository.findByServiceTypeId(serviceTypeId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Service with service type id:" + serviceTypeId + "not found.");
        }
        return result;
    }

    @Override
    public List<ServiceEntity> findByStatusId(int statusId) throws Exception {
        List<ServiceEntity> result = serviceRepository.findByServiceTypeId(statusId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Service with status id:" + statusId + "not found.");
        }
        return result;
    }
}
