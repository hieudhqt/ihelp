package com.swp.ihelp.app.service.response;

import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicetype.ServiceTypeEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse implements Serializable {
    private String id;
    private String title;
    private int spot;
    private long startDate;
    private long endDate;
    private long createdDate;
    private String accountEmail;
    private StatusEntity status;
    private ServiceTypeEntity serviceType;


    public ServiceResponse(ServiceEntity service) {
        this.id = service.getId();
        this.title = service.getTitle();
        this.startDate = service.getStartDate();
        this.endDate = service.getEndDate();
        this.accountEmail = service.getAuthorAccount().getEmail();
        this.status = service.getStatus();
        this.serviceType = service.getServiceType();
        this.createdDate = service.getCreatedDate();
    }

    public static List<ServiceResponse> convertToResponseList(List<ServiceEntity> serviceEntityList) {
        List<ServiceResponse> responseList = new ArrayList<>();
        for (ServiceEntity serviceEntity : serviceEntityList) {
            responseList.add(new ServiceResponse(serviceEntity));
        }
        return responseList;
    }
}
