package com.swp.ihelp.app.event.response;

import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.entity.StatusEntity;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.image.response.ImageResponse;
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
    private AccountGeneralResponse authorAccount;
    private StatusEntity status;
    private boolean isOnsite;
    private EventCategoryEntity category;
    private List<ImageResponse> images;

    public EventResponse(EventEntity eventEntity) {
        this.id = eventEntity.getId();
        this.title = eventEntity.getTitle();
        this.startDate = eventEntity.getStartDate();
        this.endDate = eventEntity.getEndDate();
        this.authorAccount = new AccountGeneralResponse(eventEntity.getAuthorAccount());
        this.status = eventEntity.getStatus();
        this.category = eventEntity.getEventCategory();
        this.images = ImageResponse.convertToResponseList(eventEntity.getImages());
        this.isOnsite = eventEntity.isOnsite();
    }

    public static List<EventResponse> convertToResponseList(List<EventEntity> eventEntityList) {
        List<EventResponse> responseList = new ArrayList<>();
        for (EventEntity eventEntity : eventEntityList) {
            responseList.add(new EventResponse(eventEntity));
        }
        return responseList;
    }
}
