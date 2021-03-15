package com.swp.ihelp.app.service.mapper;

import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.service.request.ServiceRequest;
import com.swp.ihelp.app.servicetype.ServiceTypeEntity;
import com.swp.ihelp.app.status.StatusEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public void updateServiceFromRequest(ServiceRequest request, ServiceEntity entity) {
        if (request.getTitle() != null)
            entity.setTitle(request.getTitle());
        if (request.getDescription() != null)
            entity.setDescription(request.getDescription());
        if (request.getLocation() != null)
            entity.setLocation(request.getLocation());
        if (request.getQuota() != 0)
            entity.setQuota(request.getQuota());
        if (request.getPoint() != 0)
            entity.setPoint(request.getPoint());
        if (request.getStartDate() != 0)
            entity.setStartDate(request.getStartDate());
        if (request.getEndDate() != 0)
            entity.setEndDate(request.getEndDate());
        if (request.getStatusId() != 0) {
            StatusEntity status = new StatusEntity().setId(request.getStatusId());
            entity.setStatus(status);
        }
        if (request.getTypeId() != 0) {
            ServiceTypeEntity type = new ServiceTypeEntity().setId(request.getTypeId());
            entity.setServiceType(type);
        }

    }
}
