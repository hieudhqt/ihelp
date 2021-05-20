package com.swp.ihelp.app.feedback.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.feedback.FeedbackEntity;
import com.swp.ihelp.app.feedbackcategory.FeedbackCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class FeedbackResponse implements Serializable {

    private String id;

    private Integer rating;

    private String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho-Chi-Minh")
    private Timestamp createdDate;

    private String email;

    private String avatarUrl;

    private String eventId;

    private String serviceId;

    private StatusEntity status;

    private FeedbackCategoryEntity feedbackCategory;

    public FeedbackResponse(FeedbackEntity feedbackEntity) {
        this.id = feedbackEntity.getId();
        this.rating = feedbackEntity.getRating();
        this.comment = feedbackEntity.getComment();
        this.createdDate = feedbackEntity.getCreatedDate();
        this.email = feedbackEntity.getAccount().getEmail();
        if (feedbackEntity.getEvent() != null) {
            this.eventId = feedbackEntity.getEvent().getId();
        } else {
            this.eventId = null;
        }
        if (feedbackEntity.getService() != null) {
            this.serviceId = feedbackEntity.getService().getId();
        }
        this.status = feedbackEntity.getStatus();
        this.feedbackCategory = feedbackEntity.getFeedbackCategory();
    }

    public static List<FeedbackResponse> convertToListResponse(List<FeedbackEntity> entityList) {
        List<FeedbackResponse> result = new ArrayList<>();
        for (FeedbackEntity entity : entityList) {
            result.add(new FeedbackResponse(entity));
        }
        return result;
    }

}