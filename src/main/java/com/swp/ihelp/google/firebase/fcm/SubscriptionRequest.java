package com.swp.ihelp.google.firebase.fcm;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SubscriptionRequest {

    private List<String> tokens;

    private String topicName;

}
