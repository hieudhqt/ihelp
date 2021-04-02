package com.swp.ihelp.app.transaction.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.transaction.TransactionEntity;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionResponse {
    private String id;
    private Integer point;
    private String description;
    private String senderEmail;
    private String receiverEmail;
    private String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp date;

    public TransactionResponse(TransactionEntity transactionEntity) {
        this.id = transactionEntity.getId();
        this.point = transactionEntity.getPoint();
        this.description = transactionEntity.getDescription();
        this.senderEmail = transactionEntity.getSenderAccount().getEmail();
        this.receiverEmail = transactionEntity.getReceiverAccount().getEmail();
        this.type = transactionEntity.getType();
        this.date = transactionEntity.getDate();
    }

    public static List<TransactionResponse> convertToResponseList(List<TransactionEntity> transactionEntities) {
        List<TransactionResponse> responseList = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            responseList.add(new TransactionResponse(transactionEntity));
        }
        return responseList;
    }
}
