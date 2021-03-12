package com.swp.ihelp.app.feedback.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.feedback.FeedbackEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class FeedbackRequest implements Serializable {
    private int rating;
    private String comment;
    private String accountEmail;

    public static FeedbackEntity convertToEntity(FeedbackRequest request) {
        AccountEntity accountEntity = new AccountEntity().setEmail(request.getAccountEmail());
        return new FeedbackEntity()
                .setRating(request.getRating())
                .setComment(request.getComment())
                .setCreatedDate(System.currentTimeMillis())
                .setAccount(accountEntity);
    }
}
