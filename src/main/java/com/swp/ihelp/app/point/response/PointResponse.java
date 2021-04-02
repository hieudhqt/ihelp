package com.swp.ihelp.app.point.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.point.PointEntity;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class PointResponse implements Serializable {
    private String id;
    private String description;
    private Integer amount;
    private Boolean isReceived;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp createdDate;
    private String accountEmail;

    public PointResponse(PointEntity entity) {
        this.id = entity.getId();
        this.amount = entity.getAmount();
        this.description = entity.getDescription();
        this.isReceived = entity.getIsReceived();
        this.createdDate = entity.getCreatedDate();
        this.accountEmail = entity.getAccount().getEmail();
    }

    public static List<PointResponse> convertToResponseList(List<PointEntity> pointEntities) {
        List<PointResponse> responseList = new ArrayList<>();
        for (PointEntity pointEntity : pointEntities) {
            responseList.add(new PointResponse(pointEntity));
        }
        return responseList;
    }
}
