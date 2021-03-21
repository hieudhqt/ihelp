package com.swp.ihelp.google.firebase.fcm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    private FCMService fcmService;

    @Autowired
    public NotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    public void sendPushNotificationToTopic(NotificationRequest request) {
        try {
            fcmService.sendMessageToTopic(request);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendPushNotificationToToken(NotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
