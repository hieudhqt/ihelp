package com.swp.ihelp.app.service.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class CreateServiceRequest implements Serializable {
    private String id;

    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotBlank(message = "Location is required.")
    private String location;

    @Min(0)
    private int quota;

    @Min(0)
    private int point;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endDate;

    @NotNull(message = "Author email cannot be null.")
    private String authorEmail;

    @NotNull(message = "Status ID cannot be null.")
    private int statusId;

    private List<Integer> categoryIds;

    private List<ImageRequest> images;

    public static ServiceEntity convertToEntity(CreateServiceRequest request) {
        AccountEntity authorAccount = new AccountEntity().setEmail(request.getAuthorEmail());
        StatusEntity serviceStatus = new StatusEntity().setId(request.getStatusId());
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        ServiceEntity service = new ServiceEntity()
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setCreatedDate(currentTimestamp)
                .setStartDate(new Timestamp(request.getStartDate().getTime()))
                .setEndDate(new Timestamp(request.getEndDate().getTime()))
                .setAuthorAccount(authorAccount)
                .setStatus(serviceStatus);
        for (int categoryId : request.getCategoryIds()) {
            service.addCategory(new ServiceCategoryEntity().setId(categoryId));
        }
        return service;
    }
}
