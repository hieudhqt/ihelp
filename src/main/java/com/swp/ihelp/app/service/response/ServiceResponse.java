package com.swp.ihelp.app.service.response;

import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse implements Serializable {
    private String id;
    private String title;
    private int spot;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createdDate;
    private String accountEmail;
    private StatusEntity status;
    private List<ServiceCategoryEntity> categories;


    public ServiceResponse(ServiceEntity service) {
        this.id = service.getId();
        this.title = service.getTitle();
        this.startDate = service.getStartDate();
        this.endDate = service.getEndDate();
        this.accountEmail = service.getAuthorAccount().getEmail();
        this.status = service.getStatus();
        this.categories = service.getCategories();
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
