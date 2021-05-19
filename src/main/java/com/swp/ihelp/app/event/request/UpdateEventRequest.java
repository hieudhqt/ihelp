package com.swp.ihelp.app.event.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.event.EventEntity;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
public class UpdateEventRequest implements Serializable {
    @NotBlank(message = "ID is required.")
    private String id;

    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotBlank(message = "Location is required.")
    private String location;

    private String longitude;

    private String latitude;

    @Min(1)
    @Max(100)
    private Integer quota;

    @Min(10)
    private Integer point;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date endDate;

    private Boolean onsite;

    @NotNull
    private Set<Integer> categoryIds;

    private String requirement;

//    private Set<UpdateImageRequest> images;

    public static EventEntity convertToEntity(UpdateEventRequest request) {
        EventEntity eventEntity = new EventEntity()
                .setId(request.getId())
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setLongitude(request.getLongitude())
                .setLatitude(request.getLatitude())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setStartDate(new Timestamp(request.getStartDate().getTime()))
                .setEndDate(new Timestamp(request.getEndDate().getTime()))
                .setRequirement(request.getRequirement())
                .setIsOnsite(request.getOnsite());
        return eventEntity;
    }

}
