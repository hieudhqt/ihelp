package com.swp.ihelp.app.service.response;

import com.swp.ihelp.app.image.response.ImageResponse;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private Integer point;
    private Integer quota;
    private Integer spot;
    private Timestamp createdDate;
    private Timestamp startDate;
    private Timestamp endDate;
    private String accountEmail;
    private StatusEntity status;
    private List<ServiceCategoryEntity> categories;
    private List<ImageResponse> images;

    public ServiceDetailResponse(ServiceEntity service) {
        this.id = service.getId();
        this.title = service.getTitle();
        this.description = service.getDescription();
        this.location = service.getLocation();
        this.point = service.getPoint();
        this.quota = service.getQuota();
        this.createdDate = service.getCreatedDate();
        this.startDate = service.getStartDate();
        this.endDate = service.getEndDate();
        this.accountEmail = service.getAuthorAccount().getEmail();
        this.images = ImageResponse.convertToResponseList(service.getImages());
        this.status = service.getStatus();
        this.categories = service.getCategories();
    }
}
