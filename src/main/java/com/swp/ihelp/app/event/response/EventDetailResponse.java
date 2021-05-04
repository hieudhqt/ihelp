package com.swp.ihelp.app.event.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.image.response.ImageResponse;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private String longitude;
    private String latitude;
    private Integer point;
    private Integer quota;
    private Integer spot;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp endDate;
    private String accountEmail;
    private String fullName;
    private String avatarUrl;
    private String reason;
    private Boolean isOnsite;
    private StatusEntity status;
    private Set<EventCategoryEntity> categories;
    private Set<ImageResponse> images;

    public EventDetailResponse(EventEntity eventEntity) {
        this.id = eventEntity.getId();
        this.title = eventEntity.getTitle();
        this.description = eventEntity.getDescription();
        this.location = eventEntity.getLocation();
        this.longitude = eventEntity.getLongitude();
        this.latitude = eventEntity.getLatitude();
        this.point = eventEntity.getPoint();
        this.quota = eventEntity.getQuota();
        this.createdDate = eventEntity.getCreatedDate();
        this.startDate = eventEntity.getStartDate();
        this.endDate = eventEntity.getEndDate();
        this.isOnsite = eventEntity.getIsOnsite();
        this.accountEmail = eventEntity.getAuthorAccount().getEmail();
        this.fullName = eventEntity.getAuthorAccount().getFullName();
        this.images = ImageResponse.convertToResponseList(eventEntity.getImages());
        this.status = eventEntity.getStatus();
        this.categories = eventEntity.getEventCategories();
        this.reason = eventEntity.getReason();
    }

}
