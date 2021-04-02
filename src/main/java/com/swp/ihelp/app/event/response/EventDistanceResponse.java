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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDistanceResponse implements Serializable {
    private String id;

    private String title;

    private Integer spot;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp createdDate;

    private String authorEmail;

    private String fullName;

    private StatusEntity status;

    private Boolean isOnsite;

    private Double distance;

    private Set<EventCategoryEntity> categories;

    private Set<ImageResponse> images;

    public EventDistanceResponse(EventEntity eventEntity) {
        this.id = eventEntity.getId();
        this.title = eventEntity.getTitle();
        this.startDate = eventEntity.getStartDate();
        this.endDate = eventEntity.getEndDate();
        this.authorEmail = eventEntity.getAuthorAccount().getEmail();
        this.fullName = eventEntity.getAuthorAccount().getFullName();
        this.status = eventEntity.getStatus();
        this.categories = eventEntity.getEventCategories();
        this.images = ImageResponse.convertToResponseList(eventEntity.getImages());
        this.isOnsite = eventEntity.getIsOnsite();
        this.createdDate = eventEntity.getCreatedDate();
    }


    public static List<EventResponse> convertToResponseList(List<EventEntity> eventEntityList) {
        List<EventResponse> responseList = new ArrayList<>();
        for (EventEntity eventEntity : eventEntityList) {
            responseList.add(new EventResponse(eventEntity));
        }
        return responseList;
    }
}
