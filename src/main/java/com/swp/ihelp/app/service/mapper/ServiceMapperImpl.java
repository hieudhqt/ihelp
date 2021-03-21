package com.swp.ihelp.app.service.mapper;

import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public void updateServiceFromRequest(UpdateServiceRequest request, ServiceEntity entity) {
//        if (request.getTitle() != null)
//            entity.setTitle(request.getTitle());
//        if (request.getDescription() != null)
//            entity.setDescription(request.getDescription());
//        if (request.getLocation() != null)
//            entity.setLocation(request.getLocation());
//        if (request.getQuota() != 0)
//            entity.setQuota(request.getQuota());
//        if (request.getPoint() != 0)
//            entity.setPoint(request.getPoint());
//        if (request.getStartDate() != 0)
//            entity.setStartDate(request.getStartDate());
//        if (request.getEndDate() != 0)
//            entity.setEndDate(request.getEndDate());
//        if (request.getStatusId() != 0) {
//            StatusEntity status = new StatusEntity().setId(request.getStatusId());
//            entity.setStatus(status);
//        }
//        if (request.getTypeId() != 0) {
//            ServiceCategoryEntity category = new ServiceCategoryEntity().setId(request.getTypeId());
//            entity.setCategories(type);
//        }

    }
}
