package com.swp.ihelp.google.firebase.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PushNotificationRequest {

    private String title;
    private String message;
    private String topic;
    private String token;

}
