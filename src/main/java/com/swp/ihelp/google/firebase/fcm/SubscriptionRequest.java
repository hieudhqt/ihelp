package com.swp.ihelp.google.firebase.fcm;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionRequest {

    private List<String> tokens;

    private String topicName;

}
