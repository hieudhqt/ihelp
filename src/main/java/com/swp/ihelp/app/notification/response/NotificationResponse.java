package com.swp.ihelp.app.notification.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.notification.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NotificationResponse implements Serializable {

    private String id;

    private String title;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp date;

    private String email;

    public NotificationResponse(NotificationEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.message = entity.getMessage();
        this.date = entity.getDate();
        this.email = entity.getAccountEntity().getEmail();
    }

    public static List<NotificationResponse> convertToListNotification(List<NotificationEntity> notificationEntities) {
        List<NotificationResponse> result = new ArrayList<>();
        for (NotificationEntity entity : notificationEntities) {
            result.add(new NotificationResponse(entity));
        }
        return result;
    }

}
