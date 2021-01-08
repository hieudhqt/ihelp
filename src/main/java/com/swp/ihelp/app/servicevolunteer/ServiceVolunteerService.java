package com.swp.ihelp.app.servicevolunteer;

import java.util.List;

// Name should be changed in the future
public interface ServiceVolunteerService {
    List<ServiceVolunteer> findAll() throws Exception;
    ServiceVolunteer findById(String id) throws Exception;
    List<ServiceVolunteer> findByTitle(String title) throws Exception;
    void save(ServiceVolunteer serviceVolunteer) throws Exception;
    void deleteById(String id) throws Exception;
}
