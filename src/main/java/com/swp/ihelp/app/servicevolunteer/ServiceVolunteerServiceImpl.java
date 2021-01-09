package com.swp.ihelp.app.servicevolunteer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceVolunteerServiceImpl implements ServiceVolunteerService {

    private ServiceVolunteerRepository serviceRepository;

    @Autowired
    public ServiceVolunteerServiceImpl(ServiceVolunteerRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ServiceVolunteer> findAll() throws Exception {
        return serviceRepository.findAll();
    }

    @Override
    public ServiceVolunteer findById(String id) throws Exception {
        Optional<ServiceVolunteer> result = serviceRepository.findById(id);
        ServiceVolunteer service = null;
        if (result.isPresent()) {
            service = result.get();
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return service;
    }

    @Override
    public List<ServiceVolunteer> findByTitle(String title) throws Exception {
        return serviceRepository.findByTitle(title);
    }

    @Override
    public void save(ServiceVolunteer serviceVolunteer) throws Exception {
        // Set createDate as current date for new service.
        Optional<ServiceVolunteer> result = serviceRepository.findById(serviceVolunteer.getId());
        if (result.isEmpty()) {
            serviceVolunteer.setCreateDate(new Date());
        }
        serviceRepository.save(serviceVolunteer);
    }

    @Override
    public void deleteById(String id) throws Exception {
        serviceRepository.deleteById(id);
    }


}
