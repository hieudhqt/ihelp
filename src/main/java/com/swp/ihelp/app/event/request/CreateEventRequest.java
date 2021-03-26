package com.swp.ihelp.app.event.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class CreateEventRequest implements Serializable {
    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotBlank(message = "Location is required.")
    private String location;

    private String longitude;

    private String latitude;

    @Min(0)
    private Integer quota;

    @Min(0)
    private Integer point;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endDate;

    @NotBlank(message = "Author email is required.")
    private String authorEmail;

    @NotNull(message = "Status ID cannot be null.")
    private int statusId;

    private Boolean onsite;

    private List<Integer> categoryIds;

    private Set<ImageRequest> images;

    public static EventEntity convertToEntity(CreateEventRequest request) {
        AccountEntity authorAccount = new AccountEntity().setEmail(request.getAuthorEmail());
        StatusEntity serviceStatus = new StatusEntity().setId(request.getStatusId());
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        EventEntity eventEntity = new EventEntity()
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setCreatedDate(currentTimestamp)
                .setStartDate(new Timestamp(request.getStartDate().getTime()))
                .setEndDate(new Timestamp(request.getEndDate().getTime()))
                .setAuthorAccount(authorAccount)
                .setIsOnsite(request.getOnsite())
                .setLongitude(request.getLongitude())
                .setLatitude(request.getLatitude())
                .setStatus(serviceStatus);
        for (int categoryId : request.getCategoryIds()) {
            eventEntity.addCategory(new EventCategoryEntity().setId(categoryId));
        }
        return eventEntity;
    }
}
