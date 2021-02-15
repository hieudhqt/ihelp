package com.swp.ihelp.app.event;

import com.swp.ihelp.app.entity.StatusEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private int point;
    private int quota;
    private long createdDate;
    private long startDate;
    private long endDate;
    private String accountEmail;
    private StatusEntity status;
    private EventCategoryEntity category;

    public EventResponse(EventEntity eventEntity) {
        this.id = eventEntity.getId();
        this.title = eventEntity.getTitle();
        this.description = eventEntity.getDescription();
        this.location = eventEntity.getLocation();
        this.point = eventEntity.getPoint();
        this.quota = eventEntity.getQuota();
        this.createdDate = eventEntity.getCreatedDate();
        this.startDate = eventEntity.getStartDate();
        this.endDate = eventEntity.getEndDate();
        this.accountEmail = eventEntity.getAuthorAccount().getEmail();
        this.status = eventEntity.getStatus();
        this.category = eventEntity.getEventCategory();
    }

    public static List<EventResponse> convertToResponseList(List<EventEntity> eventEntityList) {
        List<EventResponse> responseList = new ArrayList<>();
        for (EventEntity eventEntity : eventEntityList) {
            responseList.add(new EventResponse(eventEntity));
        }
        return responseList;
    }
}
