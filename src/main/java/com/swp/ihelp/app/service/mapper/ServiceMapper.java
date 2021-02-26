package com.swp.ihelp.app.service.mapper;

import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.service.request.ServiceRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateServiceFromRequest(ServiceRequest serviceRequest, @MappingTarget ServiceEntity entity);
}
