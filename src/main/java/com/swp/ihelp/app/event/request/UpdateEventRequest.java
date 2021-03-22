package com.swp.ihelp.app.event.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.event.EventEntity;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class UpdateEventRequest implements Serializable {
    @NotBlank(message = "ID is required.")
    private String id;

    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotBlank(message = "Location is required.")
    private String location;

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

    private boolean onsite;

    public static EventEntity convertToEntity(UpdateEventRequest request) {
        return new EventEntity()
                .setId(request.getId())
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setLocation(request.getLocation())
                .setQuota(request.getQuota())
                .setPoint(request.getPoint())
                .setStartDate(new Timestamp(request.getStartDate().getTime()))
                .setEndDate(new Timestamp(request.getEndDate().getTime()))
                .setOnsite(request.isOnsite());
    }

}
