package com.swp.ihelp.app.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.image.response.ImageResponse;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private int point;
    private int quota;
    private int spot;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endDate;
    private String accountEmail;
    private String fullName;
    private StatusEntity status;
    private Set<ServiceCategoryEntity> categories;
    private Set<ImageResponse> images;

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
        this.fullName = service.getAuthorAccount().getFullName();
        this.images = ImageResponse.convertToResponseList(service.getImages());
        this.status = service.getStatus();
        this.categories = service.getServiceCategories();
    }
}
