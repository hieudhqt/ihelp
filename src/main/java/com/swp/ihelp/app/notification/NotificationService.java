package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.notification.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> findByEmail(String email) throws Exception;

    String insert(NotificationEntity notificationEntity) throws Exception;

    void insertDeviceToken(String email, String deviceToken) throws Exception;

    void deleteDeviceToken(String email, String deviceToken) throws Exception;

    List<String> findDeviceTokensByEmail(String email) throws Exception;

}
