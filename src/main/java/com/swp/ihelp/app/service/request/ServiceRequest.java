package com.swp.ihelp.app.service.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.servicetype.ServiceTypeEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class ServiceRequest implements Serializable {
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

    @Min(0)
    private long startDate;

    @Min(0)
    private long endDate;

    @NotNull(message = "Author email cannot be null.")
    private String authorEmail;

    @NotNull(message = "Status ID cannot be null.")
    private int statusId;

    @NotNull(message = "Type ID cannot be null.")
    private int typeId;

    public static ServiceEntity convertToEntity(ServiceRequest request) {
        AccountEntity authorAccount = new AccountEntity().setEmail(request.getAuthorEmail());
        StatusEntity serviceStatus = new StatusEntity().setId(request.getStatusId());
        ServiceTypeEntity serviceType = new ServiceTypeEntity().setId(request.getTypeId());
        return new ServiceEntity()
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setCreatedDate(System.currentTimeMillis())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setAuthorAccount(authorAccount)
                .setStatus(serviceStatus)
                .setServiceType(serviceType);
    }

    public static ServiceEntity convertToEntityWithId(ServiceRequest request) {
        StatusEntity serviceStatus = new StatusEntity().setId(request.getStatusId());
        ServiceTypeEntity serviceType = new ServiceTypeEntity().setId(request.getTypeId());
        AccountEntity authorAccount = new AccountEntity().setEmail(request.getAuthorEmail());
        return new ServiceEntity()
                .setId(request.getId())
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setAuthorAccount(authorAccount)
                .setStatus(serviceStatus)
                .setServiceType(serviceType);
    }
}
