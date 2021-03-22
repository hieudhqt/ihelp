package com.swp.ihelp.app.transaction.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.transaction.TransactionEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransactionRequest implements Serializable {
    private Integer point;
    private String description;
    private String senderEmail;
    private String receiverEmail;
    private String type;

    public static TransactionEntity convertToEntity(TransactionRequest request) {
        AccountEntity senderAccount = new AccountEntity().setEmail(request.getSenderEmail());
        AccountEntity receiverAccount = new AccountEntity().setEmail(request.getReceiverEmail());
        return new TransactionEntity()
                .setPoint(request.getPoint())
                .setDescription(request.getDescription())
                .setSenderAccount(senderAccount)
                .setReceiverAccount(receiverAccount)
                .setType(request.getType());
    }
}
