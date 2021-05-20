package com.swp.ihelp.app.feedback.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.feedback.FeedbackEntity;
import com.swp.ihelp.app.feedbackcategory.FeedbackCategoryEntity;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
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
    private Integer statusId;
    private Integer feedbackCategoryId;

    public static FeedbackEntity convertToEntity(FeedbackRequest request) {
        AccountEntity accountEntity = new AccountEntity().setEmail(request.getAccountEmail());
        EventEntity eventEntity = null;
        if (!(request.getEventId() == null)) {
            eventEntity = new EventEntity().setId(request.getEventId());
        }
        ServiceEntity serviceEntity = null;
        if (!(request.getServiceId() == null)) {
            serviceEntity = new ServiceEntity().setId(request.getServiceId());
        }
        StatusEntity statusEntity = new StatusEntity().setId(request.getStatusId());
        FeedbackCategoryEntity feedbackCategoryEntity = new FeedbackCategoryEntity().setId(request.getFeedbackCategoryId());
        return new FeedbackEntity()
                .setRating(request.getRating())
                .setComment(request.getComment())
                .setCreatedDate(new Timestamp(System.currentTimeMillis()))
                .setAccount(accountEntity)
                .setEvent(eventEntity)
                .setService(serviceEntity)
                .setStatus(statusEntity)
                .setFeedbackCategory(feedbackCategoryEntity);
    }
}
