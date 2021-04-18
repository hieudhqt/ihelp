package com.swp.ihelp.google.firebase.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PushNotificationRequest {

    private String title;
    private String message;
    private String topic;
    private String token;
    private List<String> registrationTokens;
    private Map<String, String> data;

}
