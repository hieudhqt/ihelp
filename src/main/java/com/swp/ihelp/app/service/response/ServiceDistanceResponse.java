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
public class ServiceDistanceResponse implements Serializable {

    private String id;

    private String title;

    private int spot;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdDate;

    private String accountEmail;

    private String fullName;

    private StatusEntity status;

    private Double distance;

    private Set<ServiceCategoryEntity> categories;

    private Set<ImageResponse> images;

    public ServiceDistanceResponse(ServiceEntity service) {
        this.id = service.getId();
        this.title = service.getTitle();
        this.startDate = service.getStartDate();
        this.endDate = service.getEndDate();
        this.accountEmail = service.getAuthorAccount().getEmail();
        this.fullName = service.getAuthorAccount().getFullName();
        this.status = service.getStatus();
        this.categories = service.getServiceCategories();
        this.images = ImageResponse.convertToResponseList(service.getImages());
        this.createdDate = service.getCreatedDate();
    }
}
