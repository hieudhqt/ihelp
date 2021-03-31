package com.swp.ihelp.app.feedback.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.feedback.FeedbackEntity;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class FeedbackRequest implements Serializable {

    private Integer rating;
    private String comment;
    private String accountEmail;
    private String eventId;
    private String serviceId;

    public static FeedbackEntity convertToEntity(FeedbackRequest request) {
        AccountEntity accountEntity = new AccountEntity().setEmail(request.getAccountEmail());
        EventEntity eventEntity = new EventEntity().setId(request.getEventId());
        ServiceEntity serviceEntity = new ServiceEntity().setId(request.getEventId());
        StatusEntity statusEntity = new StatusEntity().setId(2);
        return new FeedbackEntity()
                .setRating(request.getRating())
                .setComment(request.getComment())
                .setCreatedDate(new Timestamp(System.currentTimeMillis()))
                .setAccount(accountEntity)
                .setEvent(eventEntity)
                .setService(serviceEntity)
                .setStatus(statusEntity);
    }
}
