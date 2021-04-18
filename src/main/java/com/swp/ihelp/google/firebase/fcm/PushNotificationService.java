package com.swp.ihelp.google.firebase.fcm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    private FCMService fcmService;

    @Autowired
    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    public void sendPushNotificationToTopic(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToTopic(request);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendPushNotificationToMultiDevices(PushNotificationRequest request) {
        try {
            fcmService.sendMessagesToMultiDevices(request);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(List<String> tokens, String topic) {
        fcmService.subscribeToTopic(new SubscriptionRequest()
        .setTokens(tokens)
        .setTopicName(topic));
    }

    public void unsubscribeToTopic(List<String> tokens, String topic) {
        fcmService.unsubscribeFromTopic(new SubscriptionRequest()
        .setTokens(tokens)
        .setTopicName(topic));

    }
}
