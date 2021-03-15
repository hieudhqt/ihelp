package com.swp.ihelp.app.event.response;

import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.image.response.ImageResponse;
import com.swp.ihelp.app.status.StatusEntity;
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
    private int spot;
    private long startDate;
    private long endDate;
    //    private AccountGeneralResponse authorAccount;
    private String authorEmail;
    private String authorFullName;
    private StatusEntity status;
    private boolean isOnsite;
    private long createdDate;
    private EventCategoryEntity category;
    private List<ImageResponse> images;

    public EventResponse(EventEntity eventEntity) {
        this.id = eventEntity.getId();
        this.title = eventEntity.getTitle();
        this.startDate = eventEntity.getStartDate();
        this.endDate = eventEntity.getEndDate();
        this.authorEmail = eventEntity.getAuthorAccount().getEmail();
        this.authorFullName = eventEntity.getAuthorAccount().getFullName();
        this.status = eventEntity.getStatus();
        this.category = eventEntity.getEventCategory();
        this.images = ImageResponse.convertToResponseList(eventEntity.getImages());
        this.isOnsite = eventEntity.isOnsite();
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
